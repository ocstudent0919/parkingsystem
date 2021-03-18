package com.parkit.parkingsystem.integration;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.constants.Promo;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;
import org.joda.time.DateTimeUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class ParkingDataBaseIT {

    private static final DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
    private static ParkingSpotDAO parkingSpotDAO;
    private static TicketDAO ticketDAO;
    private static DataBasePrepareService dataBasePrepareService;

    @Mock
    private static InputReaderUtil inputReaderUtil;

    @BeforeAll
    private static void setUp() {
        parkingSpotDAO = new ParkingSpotDAO();
        parkingSpotDAO.setDataBaseConfig(dataBaseTestConfig);
        ticketDAO = new TicketDAO();
        ticketDAO.setDataBaseConfig(dataBaseTestConfig);
        dataBasePrepareService = new DataBasePrepareService();
    }

    @BeforeEach
    private void setUpPerTest() {
        when(inputReaderUtil.readSelection()).thenReturn(1);
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
        dataBasePrepareService.clearDataBaseEntries();
        DateTimeUtils.setCurrentMillisFixed(System.currentTimeMillis());
    }

    @AfterAll
    private static void tearDown(){
        dataBasePrepareService.clearDataBaseEntries();
        DateTimeUtils.setCurrentMillisSystem();
    }

    @Test
    public void testParkingACar(){
        //GIVEN
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);

        //WHEN
        parkingService.processIncomingVehicle();

        //THEN
        //Check that the ticket is saved in the database
        assertNotNull(ticketDAO.getTicket("ABCDEF"), "Ticket was not saved");
        //Check that the parking table is updated with availability (= false)
        assertFalse(parkingSpotDAO.checkParkingAvailability(1), "Availability in the parking table was not updated");
    }

    @Test
    public void testParkingLotExit(){
        //GIVEN
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);

        //WHEN
        parkingService.processIncomingVehicle();
        DateTimeUtils.setCurrentMillisOffset(3600000); // parking duration of 1 hour
        parkingService.processExitingVehicle();

        //THEN
        //Check that the generated fare is populated correctly in the database
        assertEquals(Fare.CAR_RATE_PER_HOUR, ticketDAO.getTicket("ABCDEF").getPrice());
        //Check that the out time is populated correctly in the database
        assertNotNull(ticketDAO.getTicket("ABCDEF").getOutTime(), "Out time was not set correctly");
    }

    @Test
    public void testParkingACarRecurrentVisit() {
        //GIVEN
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);

        //WHEN
        parkingService.processIncomingVehicle(); // first time parking "ABCDEF" car
        DateTimeUtils.setCurrentMillisOffset(3600000); // parking duration of 1 hour
        parkingService.processExitingVehicle();
        DateTimeUtils.setCurrentMillisOffset(4200000); // 10 minutes break between two visits
        parkingService.processIncomingVehicle(); // second time parking "ABCDEF" car
        DateTimeUtils.setCurrentMillisOffset(7800000); // parking duration of 1 hour
        parkingService.processExitingVehicle();

        //THEN
        //Check that the ticket considered for a discount is the latest one
        assertEquals(2, ticketDAO.getTicket("ABCDEF").getId(), "Wrong ticket considered");
        //Check that the discount condition (recurrent visit) is satisfied
        assertTrue(ticketDAO.checkDiscountEligibility("ABCDEF"), "Discount eligibility check failed");
        //Check that the generated fare is populated correctly in the database
        assertEquals(Fare.CAR_RATE_PER_HOUR * (1 - Promo.REDUCTION_RATE), ticketDAO.getTicket("ABCDEF").getPrice(), "Discount application failed");
    }
}

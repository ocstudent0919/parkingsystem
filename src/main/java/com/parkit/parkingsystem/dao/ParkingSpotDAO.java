package com.parkit.parkingsystem.dao;

import com.parkit.parkingsystem.config.DataBaseConfig;
import com.parkit.parkingsystem.constants.DBConstants;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.ParkingSpot;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ParkingSpotDAO {
    private static final Logger LOGGER = LogManager.getLogger("ParkingSpotDAO");

    private DataBaseConfig dataBaseConfig = new DataBaseConfig();

    public void setDataBaseConfig(final DataBaseConfig dataBaseConfig) {
        this.dataBaseConfig = dataBaseConfig;
    }

    public int getNextAvailableSlot(final ParkingType parkingType) {
        int result = -1;
        try (Connection con = dataBaseConfig.getConnection();
             PreparedStatement ps = con.prepareStatement(DBConstants.GET_NEXT_PARKING_SPOT)) {
            ps.setString(1, parkingType.toString());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                result = rs.getInt(1);
            }
            dataBaseConfig.closeResultSet(rs);
        } catch (Exception ex) {
            LOGGER.error("Error fetching next available slot", ex);
        }
        return result;
    }

    public boolean updateParking(final ParkingSpot parkingSpot) {
        //update the availability for that parking slot
        try (Connection con = dataBaseConfig.getConnection();
             PreparedStatement ps = con.prepareStatement(DBConstants.UPDATE_PARKING_SPOT)) {
            ps.setBoolean(1, parkingSpot.isAvailable());
            ps.setInt(2, parkingSpot.getId());
            int updateRowCount = ps.executeUpdate();
            return (updateRowCount == 1);
        } catch (Exception ex) {
            LOGGER.error("Error updating parking info", ex);
            return false;
        }
    }

    public boolean checkParkingAvailability(int parkingNumber) {
        boolean available = true;
        try (Connection con = dataBaseConfig.getConnection();
             PreparedStatement ps = con.prepareStatement(DBConstants.IF_PARKING_SPOT_AVAILABLE)) {
            ps.setString(1, String.valueOf(parkingNumber));
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                available = rs.getBoolean(1);
            }
            dataBaseConfig.closeResultSet(rs);
        } catch (Exception ex) {
            LOGGER.error("Error checking parking availability", ex);
        }
        return available;
    }

}

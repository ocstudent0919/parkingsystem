package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.constants.Promo;
import com.parkit.parkingsystem.model.Ticket;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class FareCalculatorService {

    public void calculateFare(Ticket ticket){
        if( (ticket.getOutTime() == null) || (ticket.getOutTime().before(ticket.getInTime())) ){
            throw new IllegalArgumentException("Out time provided is incorrect:"+ticket.getOutTime().toString());
        }

        long inTime = ticket.getInTime().getTime();
        long outTime = ticket.getOutTime().getTime();
        double duration = getDuration(inTime, outTime);

        switch (ticket.getParkingSpot().getParkingType()){
            case CAR: {
                ticket.setPrice(duration * Fare.CAR_RATE_PER_HOUR);
                break;
            }
            case BIKE: {
                ticket.setPrice(duration * Fare.BIKE_RATE_PER_HOUR);
                break;
            }
            default: throw new IllegalArgumentException("Unkown Parking Type");
        }
    }

    private double getDuration(long inTime, long outTime){
        //Convert duration in milliseconds to duration in hours (decimal value)
        double durationInHours = (double)(outTime - inTime) / (60 * 60 * 1000);
        double duration = roundValue(durationInHours);
        //Exclude free parking time from duration;
        if(duration <= Promo.PARKING_TIME_GRATIS)
            return 0;
        return duration;
    }

    private double roundValue(Double valueToRound){
        //Round a Double to 2 decimal places using BigDecimal
        BigDecimal bd = new BigDecimal(valueToRound.toString()).setScale(2, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}
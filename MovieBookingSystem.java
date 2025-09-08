/*
- User should be able to search for movies
- User should be able to book a show
- User should be able to cancel a show
- Multiple payment options
- Notification on booking/cancel a show
 */

import javafx.scene.Scene;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;


/* ============ POJO ============ */

enum SeatType{
    Normal, Premier, Delux
}

enum SeatStatus{
    Available, Reserved, InMaintainance
}

enum PaymentMode{
    UPI, Cash, Card
}

class User{
    String userId;
    String phoneNo;
    String email;
    List<Booking> bookingList;
}

class Seat{
    String seatId;
    SeatType seatType;
    SeatStatus seatStatus;
    double price; //can use pricing strategy
}

class Movie{
    String movieId;
    String genre; //can be enum
    String movieName;
    String language;
    double duration;
}

class Show{
    String showId;
    Movie movie;
    Screen screen;
    LocalDateTime startTime;
    LocalDateTime endTime;
    Map<String,Seat> seatMap;
}

class Screen{
    String screenId;
    List<Show> shows;
}

class Theater{
    String theaterId;
    String theaterName;
    String address;
    List<Screen> screens;
}

class City{
    String cityId;
    String cityName;
    List<Theater> theaters;
}

/* ================================ */


class Booking{
    String bookingId;
    User user;
    Show show;
    List<Seat> seats;
    PaymentMode paymentMode;
}

class MovieBookingService{
    Map<String,Booking> bookingMap=new HashMap<>();

    synchronized Booking bookShow(User user, List<Seat> seats, Show show, PaymentMode paymentMode){
        double amount=0;
        for(Seat seat:seats){
            if(seat.seatStatus==SeatStatus.Available){
                amount+=seat.price;
                seat.seatStatus=SeatStatus.Reserved;
            }
        }

        PaymentFactory payment= (PaymentFactory) PaymentFactory.getPaymentMethod(paymentMode);
        payment.pay(amount);

        Booking booking=new Booking(); //constructor to set all values
        bookingMap.put(booking.bookingId, booking);
        user.bookingList.add(booking);
        return booking;
    }

    synchronized boolean cancelShow(String bookingId){
        Booking booking=bookingMap.get(bookingId);
        if(booking!=null){
            for(Seat seat:booking.seats){
                seat.seatStatus=SeatStatus.Available;
            }
            bookingMap.remove(bookingId);
            System.out.println("Cancelled Successfully");
            return true;
        }
        System.out.println("Invalid Booking Id");
        return false;
    }
}

class MovieBookingFacade{
    //Layer exposed to client
    MovieBookingService movieBookingService;

    Booking bookShow(User user, List<Seat> seats, Show show, PaymentMode paymentMode){
        return movieBookingService.bookShow(user,seats,show,paymentMode);
    }

    boolean cancelShow(String bookingId){
        return movieBookingService.cancelShow(bookingId);
    }
}


/* ============ Payment Module ============ */

interface Payment{
    void pay(double amount);
}

class UPIPayment implements Payment{
    @Override
    public void pay(double amount){

    }
}

class CardPayment implements Payment{
    @Override
    public void pay(double amount){

    }
}

class PaymentFactory{
    static public Payment getPaymentMethod(PaymentMode mode){
        switch(mode){
            case UPI: return new UPIPayment();
            case Card: return new CardPayment();
            default: throw new IllegalArgumentException("Invalid Method");
        }
    }
}

/* ================================ */


public class MovieBookingSystem {


}

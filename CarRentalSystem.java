///*
//Flow->
//Domain ===> Reposoitory(DB) ==> Service (BuisLogic) ==> Controller (Client Code)
//======= Domain =======
//Car
//Search
//PricingStrategy
//FineStrategy
//PaymentModule
//Booking
//
//======= Repository (DB Logic) =======
//CarRepository
//BookingRepository
//
//======= Service (Buisness Logic) =======
//SearchService
//CarRentalService
//
//======= Controller (Client Interaction) =======
//
//CarRentalController (Client Interaction)
//
//Client Code
//*/
//
//import java.time.Duration;
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//import java.time.Period;
//import java.time.temporal.ChronoUnit;
//import java.util.*;
//
//enum CarType{
//    SUV, Sedan
//}
//
//enum CarStatus{
//    Available, Reserved, UnderMaintainance
//}
//
//enum PaymentMode{
//    UPI, Cash, Card
//}
//
//class User{
//    String id;
//    String name;
//    String email;
//    String phoneNo;
//}
//
//class Car{
//    String carId;
//    String carName;
//    CarType carType;
//    CarStatus carStatus;
//}
//
//interface PricingStrategy{
//    double calculatePrice(Car car, LocalDateTime from, LocalDateTime to);
//}
//
//class FlatPricingStrategy implements PricingStrategy{
//    @Override
//    public double calculatePrice(Car car, LocalDateTime from, LocalDateTime to){
////        long days= ChronoUnit.DAYS.between(from,to);
//        long days= Duration.between(from,to).toDays();
//        return 100*days;
//    }
//}
//
//interface FineStrategy{
//    double calculateFine(LocalDateTime actualReturn, LocalDate currentDate);
//}
//
//class FlatFineStrategy implements FineStrategy{
//    @Override
//    public double calculateFine(LocalDateTime actualReturn, LocalDate currentDate){
//        long days= Duration.between(actualReturn,currentDate).toDays();
//        if(days<0) return 0.0;
//        return 100*days;
//    }
//}
//
//interface Payment{
//    void pay(double amount);
//}
//
//class UPIPayment implements Payment{
//    @Override
//    void pay(double amount){
//
//    }
//}
//
//class CashPayment implements Payment{
//    @Override
//    void pay(double amount){
//
//    }
//}
//
//class Booking{
//    String id;
//    User user;
//    Car car;
//    LocalDateTime from;
//    LocalDateTime to;
//    double amount;
//    PaymentMode paymentMode;
//    boolean isActive;
//
//    Booking(User user, Car car, LocalDateTime from, LocalDateTime to, PaymentMode mode){
//        this.id=UUID.randomUUID().toString();
//        this.car=car;
//        this.from=from;
//        this.to=to;
//        this.paymentMode=mode;
//        this.isActive=true;
//    }
//}
//
///* ========== Repository ========== */
//
//class CarRepository{
//    Map<String,Car> carsDb=new HashMap<>();
//
//    void addCar(Car car){
//        carsDb.put(car.carId,car);
//    }
//
//    List<Car> getAllCars(){
//        return new ArrayList<>(carsDb.values());
//    }
//
//    boolean isAvailable(String carId){
//        return carsDb.get(carId).carStatus==CarStatus.Available;
//    }
//
//    synchronized boolean reserveCar(String carId){
//        Car car=carsDb.get(carId);
//        if(car==null){
//            return false;
//        }
//
//        car.carStatus=CarStatus.Reserved;
//        carsDb.put(carId,car);
//        return true;
//    }
//
//    synchronized boolean markAvailable(String carId){
//        Car car=carsDb.get(carId);
//        if(car==null){
//            return false;
//        }
//
//        car.carStatus=CarStatus.Available;
//        carsDb.put(carId,car);
//        return true;
//    }
//}
//
//class BookingRepository{
//    Map<String,Booking> bookingsDb;
//
//    boolean addBooking(Booking booking){
//        bookingsDb.put(booking.id,booking);
//        return true;
//    }
//
//    boolean updateBooking(Booking booking){
//        bookingsDb.put(booking.id,booking);
//        return true;
//    }
//
//    Booking getBooking(String bookingId){
//        return bookingsDb.get(bookingId);
//    }
//
//    boolean isExist(String bookingId){
//        Booking booking=bookingsDb.get(bookingId);
//        return booking!=null;
//    }
//
//    boolean removeBooking(String bookingId){
//        bookingsDb.remove(bookingId);
//        return true;
//    }
//}
//
///* ========== Service ========== */
//
//class SearchService{
//    CarRepository carRepository;
//
//    List<Car> searchByName(String name){
//        List<Car> list = new ArrayList<>();
//        List<Car> carList=carRepository.getAllCars();
//        for(Car car:carList){
//            if(car.carName.equals(name)){
//                list.add(car);
//            }
//        }
//        return list;
//    }
//
//    List<Car> searchByCarType(CarType carType){
//        //similiar logic
//    }
//
//    List<Car> searchByCarStatus(CarStatus carStatus){
//        //similiar logic
//    }
//}
//
//class CarRentalService{
//    CarRepository carRepository;
//    FineStrategy fineStrategy;
//    PricingStrategy pricingStrategy;
//    BookingRepository bookingRepository;
//
//    Booking issueCar(User user, Car car, LocalDateTime from, LocalDateTime to, PaymentMode paymentMode){
//        if(!carRepository.isAvailable(car.carId)){
//            throw new IllegalArgumentException("Car not available");
//        }
//
//        carRepository.reserveCar(car.carId);
//        double amount= pricingStrategy.calculatePrice(car, from, to);
//        Payment payment=PaymentFactory.getPaymentMethod(paymentMode);
//        payment.pay(amount);
//        Booking booking=new Booking(user,car,from,to,paymentMode);
//        booking.amount=amount;
//        bookingRepository.addBooking(booking);
//        return booking;
//    }
//
//    boolean cancelBooking(String bookingId){
//        boolean isPresent=bookingRepository.isExist(bookingId);
//        if(!isPresent) return false;
//        bookingRepository.removeBooking(bookingId);
//        return true;
//    }
//
//    boolean returnCar(String bookingId){
//        Booking booking=bookingRepository.getBooking(bookingId);
//        if(booking!=null){
//            LocalDateTime now=LocalDateTime.now();
//            double fine=fineStrategy.calculateFine(booking.to,now);
//            if(fine>0){
//                //pay fine
//            }
//            carRepository.markAvailable(booking.car.carId);
//            booking.isActive=false;
//            bookingRepository.updateBooking(booking);
//            carRepository.markAvailable(booking.car.carId);
//            return true;
//        }
//        return false;
//    }
//}
//
///* ========== Controller ========== */
//
//class CarRentalController{
//    CarRentalService carRentalService;
//    SearchService searchService;
//
//    Booking issueCar(User user, Car car, LocalDateTime from, LocalDateTime to, PaymentMode mode){
//        return carRentalService.issueCar(user,car,from,to,mode);
//    }
//
//    boolean cancelBooking(String bookingId){
//        return carRentalService.cancelBooking(bookingId);
//    }
//
//    boolean returnCar(String bookingId){
//        return carRentalService.returnCar(bookingId);
//    }
//
//    List<Car> searchCarsByName(String carName){
//        return searchService.searchByName(carName);
//    }
//
//    List<Car> searchByCarType(CarType carType){
//        return searchService.searchByCarType(carType);
//    }
//
//    List<Car> searchByCarStatus(CarStatus carStatus){
//        return searchService.searchByCarStatus(carStatus);
//    }
//}
//
//class CarRentalSystem{
//    public static void main(String[] args) {
//        CarRentalController carRentalController=new CarRentalController();
//        User user=new User();
//        List<Car> cars=carRentalController.searchByCarType(CarType.Sedan);
//        Booking booking=carRentalController.issueCar(user,cars.get(0),LocalDateTime.now(),LocalDateTime.of(2025,9,12,13,30),PaymentMode.Card);
//        carRentalController.returnCar(booking.id);
//    }
//}
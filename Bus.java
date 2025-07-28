public class Bus{
    private int busNo;
    private boolean isAc;
    private int capacity;
    private int bookedSeats = 0;

    public Bus(int busNo,boolean isAc, int capacity){
        this.busNo = busNo;
        this.isAc = isAc;
        this.capacity = capacity;
    }

    public int getBusNo(){
        return busNo;
    }
   

    public boolean bookSeat(){
        if(bookedSeats < capacity){
            bookedSeats++;
            return true;
        }
        return false;
    }
    public String getInfo(){
        return "Bus no: " +busNo + "| AC: " +(isAc ? "Yes" : "No") + "| Available Seats: " +(capacity -bookedSeats);
    }
    
}
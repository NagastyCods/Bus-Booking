public class Booking {
    private String name;
    private int busNo;

    public Booking(String name, int busNo){
        this.name = name;
        this.busNo = busNo;
    }
    public String toFileString(){
        return name + "," + busNo;
    }
    public static Booking fromFileString(String line){
        String[] parts = line.split(",");
        return new Booking(parts[0], Integer.parseInt(parts[1]));
    }
    public String getInfo(){
        return "Name: " + name + " | Bus No: " + busNo;
    }
    public int getBusNo(){
        return busNo;
    }
}

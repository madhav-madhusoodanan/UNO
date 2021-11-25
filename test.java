import java.util.*;

/* 
interface Card{
    public void letsSee();
}

class SpecialCard implements Card{
    @Override
    public void letsSee(){
        System.out.println("special");
    }
}
 */

class test {
    public static void main(String[] args) {
        
       Scanner sc = new Scanner(System.in);
       String string = sc.nextLine();
       System.out.println(string.trim().equals("yo")); 
    }
}
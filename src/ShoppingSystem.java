
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

class ShoppingSystem {

    //used for any input necessary
    private Scanner kb = new Scanner(System.in);
    
    //All Information to be stored in system
    private HashMap<String, String> signInInfo;         //Id to Pass
    private HashMap<String, Customer> customerAccounts; //Id to Customer Obj
    private HashMap<String, ArrayList<Order>> allOrders;//Id to List of Orders

    private User currUser;                  //current User Obj
    private Supplier supplier = new Supplier(); //Since all suppliers use the same account
    
    private BankBot theBank = new BankBot(); //Banking System
    
    //All Instances used for User Objects 
    private User noUser = new User() {  //Has menu when no user is signed in

            @Override
            int menu(Scanner in) {
                int selection = -1;
                System.out.println("\n------------Menu------------");
                System.out.println("[c]reate account");
                System.out.println("[l]og in");
                System.out.println("[e]xit");
                
                while(selection == -1) {
                    System.out.print("Enter your choice (c, l, e):: ");
                    
                    String input = in.nextLine().replace(" ", "").toLowerCase();
                    while(input.equals(""))
                        input += in.nextLine().replace(" ", "").toLowerCase();
                    
                    switch(input.charAt(0)){
                        case 'c': selection = 2; break;
                        case 'l': selection = 0; break;
                        case 'e': selection = 8; break;
                        
                        default: System.out.println("'" + input.charAt(0) + "' is not a valid input - please try again" );
                    }
                    
                }
                
                return selection;
            }
        };
    
    public ShoppingSystem() {
        this.signInInfo = importLogIn();
        this.customerAccounts = importCustomers();
        this.allOrders = importOrders();
        
        this.currUser = noUser;
    }
    
    //Controller for Shopping System
    public void run() {
        
        while(true) {
            int operation = currUser.menu(this.kb);
            switch(operation) {
                
                case 0: //Log in Sequence
                    String[] info = new String[2];
                    this.currUser.signIn(this.kb, info);
                    
                    String infoZero = info[0];

                    if( this.signInInfo.containsKey( infoZero ) && this.signInInfo.get( infoZero ).equals(info[1]) ) {
                        if( customerAccounts.containsKey( infoZero ) ) {
                            this.currUser = customerAccounts.get( infoZero );
                            
                            System.out.println("\n\n----------------------------");
                            System.out.println("Welcome, " + ((Customer)this.currUser).getName() ); //TODO Not sure if this.currUser will be correctly cast
                            System.out.println("----------------------------");
                        } else {
                            System.out.println("\n\n----------------------------");
                            System.out.println("Welcome, Supplier");
                            System.out.println("----------------------------");
                            this.currUser = this.supplier;
                        }
                    } else {
                        System.out.println("Sorry... Either your ID or Password is incorrect");
                    }
                    
                    break;
                
                case 1: //Log out Sequence
                    this.currUser = noUser;
                    System.out.println("\nYou have successfully logged out...\n");
                    break;
                
                case 2: //Create Account Sequence
                    currUser.createAccount(this.kb, this.signInInfo, this.customerAccounts, this.allOrders);
                    System.out.println("\nAccount Successfully Created... Please Sign In...\n\n");
                    break;
                
                case 3: 
                    this.supplier.displayCatalog();
                    ((Customer)this.currUser).selectItems(this.kb, this.supplier.getItems()); //TODO Not sure if this.currUser will be correctly cast
                    break; //select items
                    
                case 4: break; //make order request
                
                case 5: 
                    ((Customer)this.currUser).viewOrder(); //TODO Not sure if this.currUser will be correctly cast
                    break; //view orders
                
                case 6: break; //process order delivery
                case 7: break; //confirm shipment
                
                case 8: //Exiting Sequence
                    exportLogIn();
                    exportCustomers();
                    exportOrders();
                    this.theBank.exportData();
                    System.exit(0);
                    break;
                    
                /**
                 * The above cases in this switch are the necessary Use Cases
                 * to complete and pass this project. The following Use Cases
                 * are extra add ons to the project in order to make the UI/UX
                 * more like a real online shopping system.
                 */
                
            }
            
            
        }
        
    }
//-----------------------Import/Export Functions--------------------------------
    
    private HashMap<String, String> importLogIn() {
        try {
            HashMap<String, String> res = new HashMap<>();

            Scanner file = new Scanner( new File( "log_in.dat" ) );
            while( file.hasNextLine() ){
                String[] info = file.nextLine().split("-");
                res.put(info[0], info[1]);
            }
            file.close();

            return res;
        } catch(Exception e) {
            System.out.println("Unable to load log in info...");
            return new HashMap<String, String>();
        }
    }
    
    private void exportLogIn() {
        //sign in data
        try {
            PrintWriter outFile = new PrintWriter("log_in.dat");        
            for(String key : this.signInInfo.keySet()){
                outFile.println(key + "-" + this.signInInfo.get(key));
            }
            outFile.close();
        } catch(Exception e) {
            System.out.println("Unable to export log in info...");
        }
    }
    
    private HashMap<String, Customer> importCustomers() {
        try {
            HashMap<String, Customer> res = new HashMap<>();

            Scanner file = new Scanner( new File( "customers.dat" ) );
            FileInputStream inStream = new FileInputStream("customers.objects");
            ObjectInputStream objectInFile = new ObjectInputStream(inStream);

            while(file.hasNextLine()){
                res.put(file.nextLine(), (Customer)objectInFile.readObject());
            }

            file.close();
            objectInFile.close();
            return res;
        } catch(Exception e) {
            System.out.println("Unable to load customer data...");
            return new HashMap<String, Customer>();
        }
    }
    
    private void exportCustomers() {
       try {         
            PrintWriter outFile = new PrintWriter("customers.dat");        
            FileOutputStream outStream = new FileOutputStream("customers.objects");
            ObjectOutputStream objectOutFile = new ObjectOutputStream(outStream);

            for(String key : customerAccounts.keySet()){
                outFile.println(key);
                objectOutFile.writeObject( customerAccounts.get(key) );
            }

            outFile.close();
            objectOutFile.close();
        } catch(Exception e) {
            System.out.println("Unable to export customer data...");
        }
    }
    
    private HashMap<String, ArrayList<Order>> importOrders() {
        try {
            HashMap<String, ArrayList<Order>> res = new HashMap<>();

            Scanner file = new Scanner( new File( "orders.dat" ) );
            FileInputStream inStream = new FileInputStream("orders.objects");
            ObjectInputStream objectInFile = new ObjectInputStream(inStream);

            while(file.hasNextLine()){
                res.put(file.nextLine(), (ArrayList<Order>)objectInFile.readObject());
            }

            file.close();
            objectInFile.close();
            return res;
        } catch(Exception e) {
            System.out.println("Unable to load order data...");
            return new HashMap<String, ArrayList<Order>>();
        }
    }
    
    private void exportOrders() {
        try {         
            PrintWriter outFile = new PrintWriter("orders.dat");        
            FileOutputStream outStream = new FileOutputStream("orders.objects");
            ObjectOutputStream objectOutFile = new ObjectOutputStream(outStream);

            for(String key : this.allOrders.keySet()) {
                outFile.println(key);
                objectOutFile.writeObject( this.allOrders.get(key) );
            }

            outFile.close();
            objectOutFile.close();
        } catch(Exception e) {
            System.out.println("Unable to export order data...");
        }
    }

}
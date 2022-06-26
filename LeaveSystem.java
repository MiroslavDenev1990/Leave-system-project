package com.project;


import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LeaveSystem {
	
    private static final Pattern VALID_EMAIL_ADDRESS_REGEX =
        Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
    private static final Pattern VALID_DATE_REGEX = Pattern.compile(
            "^\\d{2}-\\d{2}-\\d{4}$");
    private static final Pattern VALID_IDENTIFY_NUMBER_REGEX =
            Pattern.compile("^[0-9]{10}$", Pattern.CASE_INSENSITIVE);
    private static Map<String, Object> employees = new LinkedHashMap<>();
    private static final Scanner SN = new Scanner(System.in);
    private static final int NUMBER_OF_WHITE_SPACES = 15;
    private static final String APPROVED_STATUS = "approved";
    private static final String REJECTED_STATUS = "rejected";
    private static final String PENDING_STATUS = "pending";
    
    private static String nameOfPerson = "";
    private static String email = "";
    private static String identifyNumber = "";
    private static String startPeriod = "";
    private static String endPeriod = "";
    private static String typeOfLeave = "";
    private static int requestId = 1;
    
    public static void main(String[] args) {
    	do {
			switch (chooseCommand()) {
			case "1": leaveRequest(); break;
			case "2": printAllLeave(false); break;
			case "3": printLeaveOfEmployee(); break;
			case "4": changeStatusOfLeave(); break;
			case "5": return;
			}
			System.out.println("Enter a new command");
		} while (true);
	}
	
    private static void leaveRequest() {
    	validateAllData();
        Map<String, String> personalData = new LinkedHashMap<>();
        personalData.put("requestId", String.valueOf(requestId));
        requestId++;
        personalData.put("identifyNumber", identifyNumber);
        personalData.put("typeOfLeave", typeOfLeave);
        personalData.put("from", startPeriod);
        personalData.put("to", endPeriod);
        personalData.put("status", "");
        personalData.put("email", email);        
        
        employees.put(nameOfPerson, personalData);
    }
    
    private static void validateAllData() {
    	System.out.println("Enter name of person");
        nameOfPerson = SN.nextLine();
        while (!validateNameOfPerson(nameOfPerson)){
            System.out.println("Enter correct name of person");
            nameOfPerson = SN.nextLine();
        }
        System.out.println("Enter email of person");
        email = SN.nextLine();
        while (!validateEmail(email)){
            System.out.println("Invalid email! Enter correct email!");
            email = SN.nextLine();
        }
        System.out.println("Enter identify number of person");
        identifyNumber = SN.nextLine();
        while (!validateIdentifyNumber(identifyNumber)){
            System.out.println("Invalid identify number! Enter correct identify number!");
            identifyNumber = SN.nextLine();
        }
        System.out.println("Enter start period");
        startPeriod = SN.nextLine();
        while (!validateStartDate(startPeriod)){
            System.out.println("Invalid start date! Enter correct format date!");
            startPeriod = SN.nextLine();
        }
        System.out.println("Enter end period");
        endPeriod = SN.nextLine();
        while (!validateEndDate(endPeriod,startPeriod)){
            System.out.println("Invalid end date! Enter correct format date!");
            endPeriod = SN.nextLine();
        }
        System.out.println("Enter type of leave p/n");
        typeOfLeave = SN.nextLine();
        while (!validateTypeOfLeave(typeOfLeave)){
            System.out.println("Invalid type of leave! Enter correct type of leave!");
            typeOfLeave = SN.nextLine();
        }
    }
    
	private static void printAllLeave(boolean isRequestId) {
		printFirstRowOfTable(isRequestId);
		for (Map.Entry<String, Object> e : employees.entrySet()) {
			System.out.print(e.getKey());
			printWhiteSpaces(e.getKey().length());
			for (Map.Entry<String, String> b : ((LinkedHashMap<String, String>) e.getValue()).entrySet()) {
				if (isRequestId || !b.getKey().equals("requestId")) {
					System.out.print(b.getValue());
					printWhiteSpaces(b.getValue().length());
				}
			}
			System.out.println();
			System.out.println("==============================================================="
					+ "========================================================================");
		}
		System.out.println();
	}
	
	private static void printWhiteSpaces(int lengthOfWord) {
		for (int i = 0; i < NUMBER_OF_WHITE_SPACES - lengthOfWord; i++) {
			System.out.print(" ");
		}
	}
    
    private static void printFirstRowOfTable(boolean isRequestIdRequired) {
    	System.out.print("Name           ");
    	if (isRequestIdRequired) {
    		System.out.print("Request ID     ");
    	}
    	System.out.print("ID number      ");
    	System.out.print("Type of Leave  ");
    	System.out.print("From           ");
    	System.out.print("To             ");
    	System.out.print("Status         ");
    	System.out.print("Email          ");
    	System.out.println();
    	System.out.println("==============================================================="
				+ "========================================================================");
    }
    
	private static void printLeaveOfEmployee() {
		System.out.println("Enter name of employee");
		String nameOfEmployee = SN.nextLine();
		LinkedHashMap<String, String> employee = (LinkedHashMap<String, String>) employees.get(nameOfEmployee);
		if (employee == null || employee.isEmpty()) {
			System.out.println("There is no such employee with this name: " + nameOfEmployee);
			return;
		}
		printFirstRowOfTable(false);
		System.out.print(nameOfEmployee);
		printWhiteSpaces(nameOfEmployee.length());
		for (Map.Entry<String, String> a : employee.entrySet()) {
			if (!a.getKey().equals("requestId")) {
				System.out.print(a.getValue());
				printWhiteSpaces(a.getValue().length());
			}
		}
		System.out.println();
		System.out.println("==============================================================="
				+ "========================================================================");
	}
	
	private static void changeStatusOfLeave() {
		printAllLeave(true);
		
		System.out.println("Do you want to change status of employee leave? (y/n)");
		String input = "";
		while (!(input = SN.nextLine()).equals("y") && !input.equals("n")) {
			System.out.println("Invalid input! You must enter \"y\" or \"n\"!");
		}
		if (input.equals("n")) {
			return;
		}
		
		System.out.println("Enter request ID");
		String requestId = SN.nextLine();
		LinkedHashMap<String, String> leaveRequest = getLeaveRequest(requestId);
		if (leaveRequest == null || leaveRequest.isEmpty()) {
			System.out.println("There is no leave request with this ID!"
					+ " If you want to change leave status you have to press command \"4\" again!");
			return;
		}
		String leaveStatus = getLeaveStatus();
		leaveRequest.put("status", leaveStatus);
		System.out.println("Successful change status of leave request with ID: " + requestId + " on " + leaveStatus);
	}
	
	private static LinkedHashMap<String, String> getLeaveRequest(String requestId) {
		for (Map.Entry<String, Object> e : employees.entrySet()) {
			for (Map.Entry<String, String> b : ((LinkedHashMap<String, String>) e.getValue()).entrySet()) {
				if (b.getKey().equals("requestId") && b.getValue().equals(requestId)) {
					return (LinkedHashMap<String, String>) e.getValue();
				}
			}
		}
		return null;
	}
	
	private static String getLeaveStatus() {
		System.out.println("Enter status of leave. Possible statuses: approved, rejected or pending.");
		String leaveStatus = "";
		while (!(leaveStatus = SN.nextLine()).equals(APPROVED_STATUS) && !leaveStatus.equals(REJECTED_STATUS)
				&& !leaveStatus.equals(PENDING_STATUS)) {
			System.out.println("Invalid leave status! You must enter approved, pending or rejected!");
		}
		return leaveStatus;
	}

    private static boolean validateNameOfPerson(String name) {
        for (int i = 0; i < name.length(); i++) {
            if (name.charAt(i) < 65 || (name.charAt(i) > 90 && name.charAt(i) < 97) || name.charAt(i) > 122) {
                System.out.println("It's not valid name of person. Name of person must include only alphabetic characters.");
                return false;
            }
        }
        if (name.length() < 2 || name.length() > 20) {
            System.out.println("It's not valid name of person. Name of person must be range of characters: 2-20.");
            return false;
        }
        return true;
    }
    
    private static boolean validateEmail(String email) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(email);
        return matcher.matches();
    }
    
    private static boolean validateIdentifyNumber(String identifyNumber) {
        Matcher matcher = VALID_IDENTIFY_NUMBER_REGEX.matcher(identifyNumber);
        return  matcher.matches();

    }
    
    private static boolean validateStartDate(String date) {
        Matcher matcher = VALID_DATE_REGEX.matcher(date);
        return  matcher.matches();
    }
    
    private static boolean validateEndDate(String endDate, String startDate) {
        Matcher matcher = VALID_DATE_REGEX.matcher(endDate);
        if (!matcher.matches()){
            System.out.println("Invalid type of date");
            return false;
        }
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        LocalDate date1 = LocalDate.parse(endDate, dateTimeFormatter);
        LocalDate date2 = LocalDate.parse(startDate,dateTimeFormatter);
        if(date2.isAfter(date1)){
            System.out.println("End date is before start date. Input end date must be after start date");
            return false;
        }
        return  true;
    }
    
    private static boolean validateTypeOfLeave (String typeOfLeave) {
        if (!typeOfLeave.equals("p") && !typeOfLeave.equals("n")){
            System.out.println("Invalid type of leave. Type of leave must be p or n");
            return false;
        }
        return true;
    }
    
    private static String chooseCommand(){
        populateCommands();
        String command;
        while (!validateInput(command = SN.nextLine())) {
        }
        return command;
    }
    
    private static boolean validateInput(String input) {
        if (input.length() == 1 && (input.charAt(0) > 48 && input.charAt(0) < 54)) {
            return true;
        }
        System.out.println("Invalid command! Try again!");
        return false;
    }
    
    private static void populateCommands() {
    	System.out.println("---------------------------------------------");
    	System.out.println("1. Request leave");
    	System.out.println("2. Get all leave");
    	System.out.println("3. View leave of employee");
    	System.out.println("4. Change status of leave");
    	System.out.println("5. Exit");
    	System.out.println("---------------------------------------------");
    }
}
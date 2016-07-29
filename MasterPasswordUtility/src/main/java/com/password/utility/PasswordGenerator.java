package com.password.utility;

import java.util.Calendar;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PasswordGenerator {

    public static void main(String[] args) {


        System.out.println("Please enter device id shown on shown on Master Password Screen of device.");
        Scanner scanner = new Scanner(System.in);
        String imeiNumber = scanner.nextLine();

        if (imeiNumber == null && imeiNumber.equals("")) {
            System.out.println("No device id was entered.");
            return;
        }

        Calendar calendar = Calendar.getInstance();
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        int monthNumber = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);
        long imeiNumberNumeric = 0;
        if (!isDigitsOnly(imeiNumber)) {
            Pattern pattern = Pattern.compile("\\D");
            Matcher matcher = pattern.matcher(imeiNumber);
            String temp;


            while (matcher.find()) {


                temp = imeiNumber.replace(matcher.group(), ((int) matcher.group().charAt(0)) + "");
                imeiNumber = temp;
            }


        }

        if (imeiNumber.length() > 15) {
            imeiNumber = imeiNumber.substring(0, 15);
        }
        imeiNumberNumeric = Long.parseLong(imeiNumber);

        for (int i = 0; i < 5; i++) {
            long temp;
            temp = (imeiNumberNumeric - (imeiNumberNumeric / 2)) + dayOfMonth;
            temp = temp + (imeiNumberNumeric / 2) + monthNumber;
            temp = temp + year;
            imeiNumberNumeric = temp;
        }
        String masterPassword = imeiNumberNumeric + "";

        if (masterPassword.equals("0")){
            System.out.println("0");
            return ;
        }
        if (masterPassword.length() >=15) {
            masterPassword= masterPassword.substring(0, 14).substring(6, 14);
        }


        System.out.println("Master Password Calculated >>" + masterPassword);
    }

    public static boolean isDigitsOnly(CharSequence str) {
        final int len = str.length();
        for (int i = 0; i < len; i++) {
            if (!Character.isDigit(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }
}

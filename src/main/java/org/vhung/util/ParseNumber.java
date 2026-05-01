package org.vhung.util;

public class ParseNumber {
    public static double parseDouble(String numberString){
        double result = 0;
        try{
            result = Double.parseDouble(numberString.trim());
        }
        catch (NumberFormatException e){
            throw new RuntimeException("Vui lòng nhập đúng định dạng số");
        }
        return result;
    }
    public static int parseint(String numberString){
        int result = 0;
        try{
            result = Integer.parseInt(numberString.trim());
        }
        catch (NumberFormatException e){
            throw new RuntimeException("Vui lòng nhập đúng định dạng số");
        }
        return result;
    }
}

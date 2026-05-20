package org.vhung.service;

import org.vhung.System.BankSystem;
import org.vhung.dao.SystemDao;
import org.vhung.util.ParseNumber;

public class SystemService {
   private SystemDao systemDao = new SystemDao();

   public BankSystem getBankSystemConfig(){
       return systemDao.getBankSystemConfig();
   }

    public void updateConfigValue(String thongSo, String valueString){

           double value = ParseNumber.parseDouble(valueString);
           systemDao.updateConfigValue(thongSo, value);


    }

    public void updateDateSystemPlus1Month() {
        systemDao.updateDateSystemPlus1Month();
    }

    public void plusDaySystem(int days) {
        systemDao.plusDaySystem(days);
    }

    public void minusDaySystem(int days) {
        systemDao.minusDaySystem(days);
    }

    public java.time.LocalDate getTimeSystem() {
        return systemDao.getTimeSystem();
    }
}

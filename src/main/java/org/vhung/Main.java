package org.vhung;

import org.vhung.dao.SystemDao;
import org.vhung.view.LoginView;

public class Main {
    public static void main(String[] args) {
        SystemDao systemDao = new SystemDao();
        systemDao.updateDateSystemNow();
        new LoginView().display();
    }
}

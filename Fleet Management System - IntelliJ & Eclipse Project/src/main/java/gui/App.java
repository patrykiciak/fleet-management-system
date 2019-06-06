package gui;

import other.DbConnection;

import javax.swing.*;
import java.awt.*;
import java.util.Scanner;

public class App extends JFrame {

    public static void main(String... args) {
        System.out.println("Please generate a proper database for yourself. SQL Scripts are available in the attached folder.");
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter database URL: ");
        String url = scanner.nextLine();
        System.out.print("Enter user name: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();
        System.out.println("Wait for an answer...");

        try {
            DbConnection.getConnection(url, username, password);
            SwingUtilities.invokeLater(App::new);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        finally {
            try {
                DbConnection.closeConnection();
            } catch (Exception e) {
                System.err.println(e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private App() {
        super("Fleet Management System");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLayout(new BorderLayout());
        JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);

        // FIRST TAB WITHIN MAIN THREAD TO LOAD THE VIEW FASTER
        tabbedPane.add("Drivers", DriverGui.getInstance());

        this.add(tabbedPane, BorderLayout.CENTER);
        pack();
        setMinimumSize(getSize());
        setSize(640, 480);
        this.setVisible(true);

        // LOAD ANOTHER TABS
        new Thread(()-> {
            tabbedPane.add("Cars", CarGui.getInstance());
            tabbedPane.add("Earnings", EarningGui.getInstance());
            tabbedPane.add("Car Swaps", CarSwapGui.getInstance());
        }).start();
    }
}
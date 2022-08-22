import seaBattleComponents.SeaBattle;
import seaBattleDraw.SeaBattleDraw;

import javax.swing.*;
import java.util.Scanner;


public class Main {
    static int SIZE = 10;
    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);

        JFrame frame = new JFrame("Морской бой");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 1000);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.add(new SeaBattleDraw(SIZE));
        frame.setVisible(true);
    }
}

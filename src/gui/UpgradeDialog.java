package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Random;

public class UpgradeDialog extends Dialog {

    private Color color;
    private Thread thread;
    private final JLabel label;
    private final JLabel label2;
    private final JButton buyButton1;
    private final JButton buyButton2;

    public UpgradeDialog(JFrame parent) {
        super(parent, "UPGRADEN SIE JETZT!!!", true);

        JPanel panel = new JPanel(new GridLayout(3, 1));

        label = new JLabel("UPGRADEN SIE JETZT AUF DIE VOLLVERSION!!!");
        label.setFont(GuiUtils.FONT_LARGE);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(label);

        label2 = new JLabel("FÜR EINEN PREIS VON NUR 299€/Monat KÖNNEN SIE UNBEGRENZT LEIHEN ERSTELLEN!!!!!!!");
        label2.setFont(new Font("Helvetica", Font.BOLD, 22));
        label2.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(label2);

        Font buttonFont = new Font("Helvetica", Font.BOLD, 18);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buyButton1 = new JButton("!JETZT KAUFEN!");
        buyButton1.setFont(buttonFont);
        buttons.add(buyButton1);

        buyButton2 = new JButton("!SOFORT KAUFEN!");
        buyButton2.setFont(buttonFont);
        buttons.add(buyButton2);

        panel.add(buttons);
        add(panel, BorderLayout.CENTER);

        thread = new Thread(() -> {
            Random random = new Random();
            while (!thread.isInterrupted()) {
                label.setForeground(new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256)));
                label2.setForeground(new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256)));
                buyButton1.setForeground(new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256)));
                buyButton2.setForeground(new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256)));
                panel.repaint();
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    thread.interrupt();
                }
            }
        });
        thread.start();

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                thread.interrupt();
                dispose();
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                super.mouseMoved(e);
                setLocation(e.getXOnScreen() - (buyButton1.getX() + 50), e.getYOnScreen() - 200);
            }
        });

        setSize(1200, 250);
        setVisible(true);
    }
}

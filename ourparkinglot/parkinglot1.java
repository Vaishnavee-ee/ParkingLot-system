import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.text.SimpleDateFormat;

public class parkinglot1 {
    static HashMap<Integer, Vehicle> parkingSlots = new HashMap<>();
    static final int TOTAL_SLOTS = 10;
    static JFrame frame;
    static JLayeredPane layeredPane;
    static JLabel backgroundLabel;
    static JPanel slotPanel;
    static ArrayList<JLabel> vehicleLabels = new ArrayList<>();

    public static void main(String[] args) {
        SwingUtilities.invokeLater(parkinglot1::createGUI);//         ?
    }

    public static void createGUI() {
    frame = new JFrame("Parking Lot System");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setSize(1200, 800);
    frame.setLayout(null);

  

    layeredPane = new JLayeredPane();
    layeredPane.setBounds(0, 0, frame.getWidth(), frame.getHeight());


    slotPanel = new JPanel();
    slotPanel.setLayout(new GridLayout(0, 5, 20, 20));
    slotPanel.setOpaque(false);

    JScrollPane scrollPane = new JScrollPane(slotPanel);
    scrollPane.setBounds(50, 50, 1100, 500);
    scrollPane.setOpaque(false);
    scrollPane.getViewport().setOpaque(false);
    scrollPane.setBorder(null);

    layeredPane.add(scrollPane, JLayeredPane.PALETTE_LAYER);

    JButton parkButton = new JButton("Park Vehicle");
    JButton unparkButton = new JButton("Unpark Vehicle");
    JButton refreshButton = new JButton("Refresh");

    parkButton.addActionListener(e -> parkVehicle());
    unparkButton.addActionListener(e -> unparkVehicle());
    refreshButton.addActionListener(e -> updateDisplay());

    JPanel buttonPanel = new JPanel();
    buttonPanel.add(parkButton);
    buttonPanel.add(unparkButton);
    buttonPanel.add(refreshButton);
    buttonPanel.setBounds(50, 580, 1100, 50);

   frame.setContentPane(layeredPane); 
    frame.add(buttonPanel);
    frame.setVisible(true);

    updateDisplay();
}


    public static void parkVehicle() {
        for (int i = 1; i <= TOTAL_SLOTS; i++) {
            if (!parkingSlots.containsKey(i)) {
                String[] options = {"Car", "Bike"};
                int choice = JOptionPane.showOptionDialog(
                        frame, "Select vehicle type:", "Vehicle Type",
                        JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE,
                        null, options, options[0]);
                if (choice == -1) return;

                String plate = JOptionPane.showInputDialog("Enter vehicle plate number:");
                if (plate == null || plate.trim().isEmpty()) return;

                int imageIndex = (options[choice].equals("Car")) ? new Random().nextInt(5) + 1 : 1;
                Vehicle v = new Vehicle(plate, options[choice], i, imageIndex);
                parkingSlots.put(i, v);
                v.startTimer();
                updateDisplay();
                return;
            }
        }
        JOptionPane.showMessageDialog(frame, "No available slots!");
    }

    public static void unparkVehicle() {
        String slotStr = JOptionPane.showInputDialog("Enter slot number to unpark:");
        if (slotStr == null) return;

        try {
            int slot = Integer.parseInt(slotStr.trim());

            if (parkingSlots.containsKey(slot)) {
                Vehicle v = parkingSlots.remove(slot);
                v.stopTimer();

                long duration = (new Date().getTime() - v.startTime.getTime()) / (1000 * 60 * 60);
                if (duration == 0) duration = 1;
                long charge = duration * 20;

                JOptionPane.showMessageDialog(frame,
                        "Vehicle Plate: " + v.plate + "\nSlot: " + slot + "\nTotal Charge: Rs. " + charge);

                updateDisplay();
            } else {
                JOptionPane.showMessageDialog(frame, "No vehicle found in Slot " + slot + ".");
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(frame, "Invalid slot number entered!");
        }
    }

   public static void updateDisplay() {
    slotPanel.removeAll();
    vehicleLabels.clear();

    for (int i = 1; i <= TOTAL_SLOTS; i++) {
        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setBackground(new Color(34, 139, 34)); // Forest Green

        container.setOpaque(true);

        if (parkingSlots.containsKey(i)) {
            Vehicle v = parkingSlots.get(i);


            ImageIcon icon;
            if (v.type.equalsIgnoreCase("car")) {
                ImageIcon rawIcon = null;
           try {
    String path = "assetslot/car_top_" + v.imageIndex + ".png";
    System.out.println("Loading image for Slot " + i + ": " + path); // Debug log
    rawIcon = new ImageIcon(path);

           if (rawIcon.getIconWidth() == -1) {
        throw new Exception("Image not found");
    }

           icon = rawIcon;
}         
          catch (Exception e) {
    System.out.println("Image for car in slot " + i + " not found. Using fallback image.");
    icon = new ImageIcon("assetslot/car_top_1.png"); 
}

                icon = new ImageIcon("assetslot/car_top_" + v.imageIndex + ".png");
            } 
            else {
                ImageIcon rawBikeIcon = new ImageIcon("assetslot/bike_top.png");
                Image scaledBike = rawBikeIcon.getImage().getScaledInstance(100, 150, Image.SCALE_SMOOTH);
                icon = new ImageIcon(scaledBike);
            }

            JLabel iconLabel = new JLabel(icon);
            JLabel slotLabel = new JLabel("Slot " + i, SwingConstants.CENTER);
            JLabel plateLabel = new JLabel(v.plate, SwingConstants.CENTER);
            JLabel timerLabel = v.timerLabel;

            iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            slotLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            plateLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            timerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

            slotLabel.setFont(new Font("Arial", Font.BOLD, 12));
            plateLabel.setFont(new Font("Arial", Font.PLAIN, 11));
            timerLabel.setFont(new Font("Arial", Font.PLAIN, 11));

            container.add(iconLabel);
            container.add(Box.createVerticalStrut(5));
            container.add(slotLabel);
            container.add(plateLabel);
            container.add(timerLabel);

            vehicleLabels.add(iconLabel);
        } else {
            JLabel emptyLabel = new JLabel("Slot " + i + " (Empty)", SwingConstants.CENTER);
            emptyLabel.setFont(new Font("Arial", Font.PLAIN, 12));
            container.add(Box.createVerticalGlue());
            container.add(emptyLabel);
            container.add(Box.createVerticalGlue());
        }

        slotPanel.add(container);
    }

    slotPanel.revalidate();
    slotPanel.repaint();
}
}

class Vehicle {
    String plate;
    String type;
    Date startTime;
    java.util.Timer timer;
    int slot;
    int imageIndex;
    JLabel timerLabel;
    int secondsElapsed = 0;

    Vehicle(String plate, String type, int slot, int imageIndex) {
        this.plate = plate;
        this.type = type;
        this.slot = slot;
        this.imageIndex = imageIndex;
        this.startTime = new Date();
     

    }

    void startTimer() {
        timerLabel = new JLabel("0s", SwingConstants.CENTER);
        timerLabel.setForeground(Color.BLACK);
        timerLabel.setFont(new Font("Arial", Font.PLAIN, 11));

        timer = new java.util.Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                secondsElapsed++;
                SwingUtilities.invokeLater(() -> timerLabel.setText(secondsElapsed + "s"));
            }
        }, 0, 1000);
    }

    void stopTimer() {
        if (timer != null) timer.cancel();.
        
    }

    String getFormattedTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        return sdf.format(startTime);
    }
}


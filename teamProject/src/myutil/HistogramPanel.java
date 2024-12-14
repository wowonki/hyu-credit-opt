package myutil;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class HistogramPanel extends JPanel {
    private List<double[]> histogram;

    public HistogramPanel(List<double[]> histogram) {
        this.histogram = histogram;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;
        int width = getWidth();
        int height = getHeight();
        int padding = 50;
        int barWidth = (width - 2 * padding) / histogram.size();

        // Max count for scaling
        int maxCount = histogram.stream().mapToInt(data -> (int) data[2]).max().orElse(1);

        // Draw bars
        for (int i = 0; i < histogram.size(); i++) {
            double[] data = histogram.get(i);
            double lowerBound = data[0];
            double upperBound = data[1];
            int count = (int) data[2];

            // Bar height based on count
            int barHeight = (int) ((double) count / maxCount * (height - 2 * padding));

            // Calculate bar position
            int x = padding + i * barWidth;
            int y = height - padding - barHeight;

            // Draw bar
            g2d.setColor(Color.BLUE);
            g2d.fillRect(x, y, barWidth - 5, barHeight);

            // Draw labels
            g2d.setColor(Color.BLACK);
            g2d.drawString(String.format("%.2f~%.2f", lowerBound, upperBound), x, height - padding + 15);
            g2d.drawString(String.valueOf(count), x + barWidth / 4, y - 5);
        }
    }
}

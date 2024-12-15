package myutil; 

import java.util.*;
import java.util.List;

import javax.swing.*;
import java.awt.*;

public class ScatterPlotSimple {

    public static void drawScatterPlot(double[] xData, double[] yData, String title) {
        JFrame frame = new JFrame(title);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 1000);

        // Efficient Frontier 데이터 계산
        TreeMap<Double, Double> efficientFrontier = new TreeMap<>();
        List<double[]> dataPoints = new ArrayList<>();

        // 데이터를 리스트로 변환
        for (int i = 0; i < xData.length; i++) {
            dataPoints.add(new double[]{xData[i], yData[i]});
        }

        // 리스크를 기준으로 정렬
        dataPoints.sort(Comparator.comparingDouble(a -> a[0]));

        double maxReturn = Double.NEGATIVE_INFINITY;
        for (double[] point : dataPoints) {
            double risk = point[0];
            double ret = point[1];
            if (ret > maxReturn) {
                maxReturn = ret;
                efficientFrontier.put(risk, ret); // 외곽 점 추가
            }
        }

        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int padding = 100;
                int width = getWidth() - 2 * padding;
                int height = getHeight() - 2 * padding;

                g2d.drawLine(padding, getHeight() - padding, padding, padding);
                g2d.drawLine(padding, getHeight() - padding, getWidth() - padding, getHeight() - padding);

                g2d.setColor(Color.BLACK);
                FontMetrics metrics = g2d.getFontMetrics();
                String yLabel = "Return";
                String xLabel = "Risk";
                g2d.drawString(yLabel, padding / 2 - metrics.stringWidth(yLabel) / 2, getHeight() / 2);
                g2d.drawString(xLabel, getWidth() - padding - metrics.stringWidth(xLabel) / 2, getHeight() - padding + 30);

                double xMin = Double.MAX_VALUE, xMax = Double.MIN_VALUE;
                double yMin = Double.MAX_VALUE, yMax = Double.MIN_VALUE;
                for (int i = 0; i < xData.length; i++) {
                    xMin = Math.min(xMin, xData[i]);
                    xMax = Math.max(xMax, xData[i]);
                    yMin = Math.min(yMin, yData[i]);
                    yMax = Math.max(yMax, yData[i]);
                }

                double xRange = xMax - xMin;
                double yRange = yMax - yMin;

                xMin -= 0.1 * xRange;
                xMax += 0.1 * xRange;
                yMin -= 0.1 * yRange;
                yMax += 0.1 * yRange;

                // label
                g2d.setColor(Color.LIGHT_GRAY);
                for (int i = 0; i <= 10; i++) {
                    int xGrid = padding + (int) ((i / 10.0) * width);
                    double xValue = xMin + (i / 10.0) * (xMax - xMin);
                    g2d.drawLine(xGrid, getHeight() - padding, xGrid, padding);
                    g2d.setColor(Color.BLACK);
                    g2d.drawString(String.format("%.4f", xValue), xGrid - 15, getHeight() - padding + 20);
                    g2d.setColor(Color.LIGHT_GRAY);

                    int yGrid = getHeight() - padding - (int) ((i / 10.0) * height);
                    double yValue = yMin + (i / 10.0) * (yMax - yMin);
                    g2d.drawLine(padding, yGrid, getWidth() - padding, yGrid);
                    g2d.setColor(Color.BLACK);
                    g2d.drawString(String.format("%.4f", yValue), padding - 50, yGrid + 5);
                    g2d.setColor(Color.LIGHT_GRAY);
                }

                g2d.setColor(Color.BLUE);
                for (int i = 0; i < xData.length; i++) {
                    int x = (int) (padding + (xData[i] - xMin) / (xMax - xMin) * width);
                    int y = (int) (getHeight() - padding - (yData[i] - yMin) / (yMax - yMin) * height);
                    g2d.fillOval(x - 2, y - 2, 4, 4);
                }

                // Efficient Frontier 그리기
                g2d.setColor(Color.RED);
                int prevX = -1, prevY = -1;
                for (Map.Entry<Double, Double> entry : efficientFrontier.entrySet()) {
                    int x = (int) (padding + (entry.getKey() - xMin) / (xMax - xMin) * width);
                    int y = (int) (getHeight() - padding - (entry.getValue() - yMin) / (yMax - yMin) * height);
                    if (prevX != -1 && prevY != -1) {
                        g2d.drawLine(prevX, prevY, x, y);
                    }
                    prevX = x;
                    prevY = y;
                }

                g2d.setColor(Color.BLACK);
                g2d.drawString(title, getWidth() / 2 - g2d.getFontMetrics().stringWidth(title) / 2, padding / 2);
            }
        };

        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // 창만 닫기
        frame.add(panel);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        double[] xData = new double[20000];
        double[] yData = new double[20000];
        for (int i = 0; i < 20000; i++) {
            xData[i] = Math.random() * 100;
            yData[i] = Math.random() * 100;
        }

        drawScatterPlot(xData, yData, "Efficient Frontier Example");
    }
}

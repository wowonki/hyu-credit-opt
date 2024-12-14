package myutil;

import javax.swing.*;
import java.awt.*;

public class ScatterPlotSimple {

    // 메서드: xData와 yData를 입력하면 Scatter Plot을 그리는 메서드
    public static void drawScatterPlot(double[] xData, double[] yData, String title) {
        // JFrame 생성
        JFrame frame = new JFrame(title);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 1000);

        // JPanel에 Scatter Plot 데이터 전달
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int padding = 100;
                int width = getWidth() - 2 * padding;
                int height = getHeight() - 2 * padding;

                // X축 및 Y축 그리기
                g2d.drawLine(padding, getHeight() - padding, padding, padding); // Y축
                g2d.drawLine(padding, getHeight() - padding, getWidth() - padding, getHeight() - padding); // X축

                // X축, Y축 레이블
                g2d.setColor(Color.BLACK);
                FontMetrics metrics = g2d.getFontMetrics();
                String yLabel = "Return";
                String xLabel = "Risk";
                g2d.drawString(yLabel, padding / 2 - metrics.stringWidth(yLabel) / 2, getHeight() / 2); // Y축 레이블
                g2d.drawString(xLabel, getWidth() - padding - metrics.stringWidth(xLabel) / 2, getHeight() - padding + 30); // X축 레이블

                // 데이터 범위 계산
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

                xMin -= 0.1 * xRange; // xMin에 10% 여유 추가
                xMax += 0.1 * xRange; // xMax에 10% 여유 추가
                yMin -= 0.1 * yRange; // yMin에 10% 여유 추가
                yMax += 0.1 * yRange; // yMax에 10% 여유 추가

                // 그리드 및 값 표시
                g2d.setColor(Color.LIGHT_GRAY);
                for (int i = 0; i <= 10; i++) {
                    // X축 그리드 및 값
                    int xGrid = padding + (int) ((i / 10.0) * width);
                    double xValue = xMin + (i / 10.0) * (xMax - xMin);
                    g2d.drawLine(xGrid, getHeight() - padding, xGrid, padding); // 그리드 선
                    g2d.setColor(Color.BLACK);
                    g2d.drawString(String.format("%.2f", xValue), xGrid - 15, getHeight() - padding + 20); // 값 표시
                    g2d.setColor(Color.LIGHT_GRAY);

                    // Y축 그리드 및 값
                    int yGrid = getHeight() - padding - (int) ((i / 10.0) * height);
                    double yValue = yMin + (i / 10.0) * (yMax - yMin);
                    g2d.drawLine(padding, yGrid, getWidth() - padding, yGrid); // 그리드 선
                    g2d.setColor(Color.BLACK);
                    g2d.drawString(String.format("%.6f", yValue), padding - 50, yGrid + 5); // 값 표시
                    g2d.setColor(Color.LIGHT_GRAY);
                }

                // 데이터 점 그리기
                g2d.setColor(Color.BLUE);
                for (int i = 0; i < xData.length; i++) {
                    int x = (int) (padding + (xData[i] - xMin) / (xMax - xMin) * width);
                    int y = (int) (getHeight() - padding - (yData[i] - yMin) / (yMax - yMin) * height);
                    g2d.fillOval(x - 2, y - 2, 4, 4); // 점을 작은 원으로 표시
                }

                // 그래프 제목
                g2d.setColor(Color.BLACK);
                g2d.drawString(title, getWidth() / 2 - g2d.getFontMetrics().stringWidth(title) / 2, padding / 2);
            }
        };

        frame.add(panel);
        frame.setVisible(true);
    }

    // Main 메서드: 사용 예시
    public static void main(String[] args) {
        // 데이터 생성
        double[] xData = new double[20000];
        double[] yData = new double[20000];
        for (int i = 0; i < 20000; i++) {
            xData[i] = Math.random() * 100; // 0 ~ 100 사이의 랜덤 데이터
            yData[i] = Math.random() * 100;
        }

        // Scatter Plot 그리기
        drawScatterPlot(xData, yData, "Sample Scatter Plot");
    }
}

package myutil;

import java.io.*;
import java.util.*;

public class Maps {
    private HashMap<Integer, Double[]> weightMap = new HashMap<>();
    private HashMap<Integer, StatusNode> statusMap = new HashMap<>();
    private HashMap<Integer, Double[]> valueMap = new HashMap<>(); // Placeholder for valueMap (구현 필요)

    // 생성자: 파일 경로를 받아 자동으로 Map 구성
    public Maps(String weightFilePath, String statusFilePath, String valueFilePath) {
        loadWeightCsv(weightFilePath);  // weightMap 구성
        loadStatusCsv(statusFilePath); // statusMap 구성
        loadValueCsv(valueFilePath);   // valueMap 구성 (추가 구현 필요)
    }

    // Weight CSV 파일 읽기
    public void loadWeightCsv(String filePath) {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            int problemNumber = 0;

            while ((line = br.readLine()) != null) {
                String[] weights = line.split(",");
                Double[] weightarray = new Double[3000];

                for (int i = 0; i < 3000; i++) {
                    weightarray[i] = Double.parseDouble(weights[i].trim());
                }

                weightMap.put(problemNumber, weightarray);
                problemNumber++;
            }
        } catch (IOException e) {
            System.err.println("Weight CSV 파일 읽기 중 오류 발생: " + e.getMessage());
        }
    }

    // Status CSV 파일 읽기
    public void loadStatusCsv(String filePath) {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            br.readLine(); // 헤더 스킵

            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                int problemNumber = Integer.parseInt(values[0]);
                boolean optimality = Boolean.parseBoolean(values[1]);
                double timeTaken = Double.parseDouble(values[2]);
                int variableCount = Integer.parseInt(values[3]);

                StatusNode node = new StatusNode(optimality, timeTaken, variableCount);
                statusMap.put(problemNumber, node);
            }
        } catch (IOException e) {
            System.err.println("Status CSV 파일 읽기 중 오류 발생: " + e.getMessage());
        }
    }

    // Value CSV 파일 읽기 (Placeholder, 구현 필요)
    public void loadValueCsv(String filePath) {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            int problemNumber = 0;

            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                Double[] valuearray = new Double[2];

                for (int i = 0; i < 2; i++) {
                    valuearray[i] = Double.parseDouble(values[i].trim());
                }

                weightMap.put(problemNumber, valuearray);
                problemNumber++;
            }
        } catch (IOException e) {
            System.err.println("Weight CSV 파일 읽기 중 오류 발생: " + e.getMessage());
        }
    }

    // Getter 메서드
    public HashMap<Integer, Double[]> getWeightMap() {
        return weightMap;
    }

    public HashMap<Integer, StatusNode> getStatusMap() {
        return statusMap;
    }

    public HashMap<Integer, Double[]> getValueMap() {
        return valueMap;
    }

    // 내부 클래스: StatusNode
    class StatusNode {
        private boolean optimality;
        private double timeTaken;
        private int variableCount;

        public StatusNode(boolean optimality, double timeTaken, int variableCount) {
            this.optimality = optimality;
            this.timeTaken = timeTaken;
            this.variableCount = variableCount;
        }

        public boolean isOptimality() {
            return optimality;
        }

        public double getTimeTaken() {
            return timeTaken;
        }

        public int getVariableCount() {
            return variableCount;
        }

        @Override
        public String toString() {
            return "StatusNode{" +
                    "optimality=" + optimality +
                    ", timeTaken=" + timeTaken +
                    ", variableCount=" + variableCount +
                    '}';
        }
    }
}
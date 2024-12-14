package service;

import myutil.HistogramPanel;
import myutil.Maps;
import myutil.Maps.StatusNode; // StatusNode 클래스 직접 사용
import myutil.ScatterPlotSimple;

import javax.swing.*;
import java.util.*;

public class MyService {

    private Maps maps; // Maps 객체를 필드로 선언

    public MyService() {
        // Maps 객체 초기화
        maps = new Maps("result_weight_v2.csv", "result_status.csv", "result_value.csv");
    }

    /**
     * 모든 문제의 총 걸린 시간을 계산합니다.
     *
     * @return 총 걸린 시간 (초 단위)
     */
    public double getTotalTime() {
        double totalTime = 0.0;

        // statusMap에서 데이터를 가져와 합산
        for (Map.Entry<Integer, StatusNode> entry : maps.getStatusMap().entrySet()) {
            totalTime += entry.getValue().getTimeTaken();
        }

        return totalTime;
    }

    /**
     * 특정 변수를 포함한 문제들의 평균 소요 시간을 계산합니다.
     *
     * @param variableIndex 포함 여부를 확인할 변수 번호 (0-based index)
     * @return 평균 소요 시간 (초 단위)
     */
    public double getAverageTimeForVariable(int variableIndex) {
        double totalTime = 0.0;
        int count = 0;

        // statusMap 순회
        for (Map.Entry<Integer, StatusNode> entry : maps.getStatusMap().entrySet()) {
            int problemId = entry.getKey();
            StatusNode statusNode = entry.getValue();

            // 특정 변수를 포함하는지 확인
            Double[] weights = maps.getWeightMap().get(problemId);
            if (weights != null && variableIndex >= 0 && variableIndex < weights.length) {
                if (weights[variableIndex] != null && weights[variableIndex] != 0) {
                    totalTime += statusNode.getTimeTaken();
                    count++;
                }
            }
        }

        // 평균 계산
        if (count == 0) {
            return 0.0; // 조건에 맞는 문제가 없을 때
        }
        return totalTime / count;
    }

    /**
     * 특정 시간 범위 내에 풀린 문제 번호를 반환합니다.
     * lowerBound를 기본값(0.0)으로 설정합니다.
     *
     * @param upperBound 최대 시간 (초 단위)
     * @return 특정 시간 범위 내에 풀린 문제들의 번호 리스트
     */
    public List<Integer> getProblemsSolvedWithinTime(double upperBound) {
        return getProblemsSolvedWithinTime(upperBound, 0.0); // lowerBound 기본값
    }

    /**
     * 특정 시간 범위 내에 풀린 문제 번호를 반환합니다.
     *
     * @param upperBound 최대 시간 (초 단위)
     * @param lowerBound 최소 시간 (초 단위, 기본값: 0.0)
     * @return 특정 시간 범위 내에 풀린 문제들의 번호 리스트
     */
    public List<Integer> getProblemsSolvedWithinTime(double upperBound, double lowerBound) {
        // lowerBound 기본값 처리
        if (lowerBound < 0) {
            lowerBound = 0.0; // 음수 값 방지
        }

        List<Integer> result = new ArrayList<>();

        // statusMap에서 문제 번호와 상태를 가져와 필터링
        for (Map.Entry<Integer, StatusNode> entry : maps.getStatusMap().entrySet()) {
            double timeTaken = entry.getValue().getTimeTaken();
            if (timeTaken >= lowerBound && timeTaken <= upperBound) {
                result.add(entry.getKey());
            }
        }

        return result;
    }

    /**
     * 문제 번호에 해당하는 수익률을 반환합니다.
     *
     * @param problemId 문제 번호
     * @return 수익률 값 (Double) 또는 null (문제가 없는 경우)
     */
    public Double getProfitByProblemId(int problemId) {
        // valueMap에서 문제 번호에 해당하는 값을 가져옴
        Double[] values = maps.getValueMap().get(problemId);

        if (values == null || values.length < 2) {
            return null; // 값이 없거나 수익률 데이터가 없는 경우
        }

        return values[1]; // 두 번째 값이 수익률
    }

    /**
     *최적화가 완료된 문제의 비율(%)을 계산하여 반환합니다.
     *
     * @return 최적화된 문제의 비율(%) (소수점 포함)
     */
    public float getProblemSolvedPercentage() {
    	
    	float solved = 0;
    	float count = 0;
    	float result;
    	for (Map.Entry<Integer, StatusNode> entry : maps.getStatusMap().entrySet()) {
    		
    		if (entry.getValue().isOptimality()) {
    			solved ++;
    		}
    		count++;
    	}
    	result = (solved / count) * 100;
    	return result;
    }

    /**
     *Efficient Frontier 을 시각화합니다.
     */
    public void drawEfficientFrontier() {
    	
    	double[] xData = new double[maps.getValueMap().size()];
        double[] yData = new double[maps.getValueMap().size()];

        int index = 0;
        for (Map.Entry<Integer, Double[]> entry : maps.getValueMap().entrySet()) {
            xData[index] = -entry.getValue()[0]; // risk
            yData[index] = entry.getValue()[1]; // return
            index++;
        }
    	
    	ScatterPlotSimple.drawScatterPlot(xData, yData, "Sample Scatter Plot");
    }

    /**
     * valueMap에서 정렬된 수익률 리스트를 반환합니다.
     *
     * @return 정렬된 수익률 리스트
     */
    public List<Double> getSortedProfits() {
        TreeMap<Double, Integer> profitFrequency = new TreeMap<>(); // 수익률 -> 빈도수

        // valueMap에서 수익률 데이터를 TreeMap에 추가
        for (Map.Entry<Integer, Double[]> entry : maps.getValueMap().entrySet()) {
            Double[] values = entry.getValue();
            if (values != null && values.length >= 2) {
                double profit = values[1]; // 수익률
                profitFrequency.put(profit, profitFrequency.getOrDefault(profit, 0) + 1);
            }
        }

        // TreeMap에서 정렬된 수익률 데이터를 리스트로 변환
        List<Double> profits = new ArrayList<>();
        for (Map.Entry<Double, Integer> entry : profitFrequency.entrySet()) {
            double profit = entry.getKey();
            int frequency = entry.getValue();
            for (int i = 0; i < frequency; i++) {
                profits.add(profit);
            }
        }

        return profits;
    }

    /**
     * 정렬된 수익률 리스트를 기반으로 지정된 분위 개수로 나누어 평균을 계산합니다.
     *
     * @param sortedProfits 정렬된 수익률 리스트
     * @param divisions 분위 개수 (예: 10, 20 등)
     * @return 분위별 평균 수익률 리스트
     */
    public List<Double> calculateAveragesFromSortedList(List<Double> sortedProfits, int divisions) {
        List<Double> averages = new ArrayList<>();
        int total = sortedProfits.size();
        int chunkSize = total / divisions; // 한 분위의 데이터 개수

        for (int i = 0; i < divisions; i++) {
            int start = i * chunkSize;
            int end = (i == divisions - 1) ? total : start + chunkSize; // 마지막 분위는 남은 데이터 포함

            List<Double> quantile = sortedProfits.subList(start, end);
            averages.add(calculateAverage(quantile));
        }

        return averages;
    }

    /**
     * 리스트의 평균을 계산합니다.
     *
     * @param values 수익률 리스트
     * @return 평균 값
     */
    private Double calculateAverage(List<Double> values) {
        if (values.isEmpty()) return 0.0;

        double sum = 0.0;
        for (Double value : values) {
            sum += value;
        }
        return sum / values.size();
    }

    /**
     * 정렬된 수익률 리스트를 기반으로 분위별 범위를 나누고, 각 범위에 속하는 데이터의 개수를 계산합니다.
     *
     * @param sortedProfits 정렬된 수익률 리스트
     * @param quantile 분위 개수 (예: 10, 20 등)
     * @return 분위별 데이터 배열 (각 분위의 lowerBound, upperBound, count를 포함)
     */
    public List<double[]> generateHistogram(List<Double> sortedProfits, int quantile) {
        List<double[]> histogram = new ArrayList<>();

        // 리스트의 최소값과 최대값
        double minProfit = sortedProfits.get(0);
        double maxProfit = sortedProfits.get(sortedProfits.size() - 1);

        // 각 분위의 범위 크기
        double range = (maxProfit - minProfit) / quantile;

        // 분위별 데이터 초기화
        for (int i = 0; i < quantile; i++) {
            double lowerBound = minProfit + i * range;
            double upperBound = (i == quantile - 1) ? maxProfit : lowerBound + range;
            histogram.add(new double[]{lowerBound, upperBound, 0});
        }

        // 각 데이터가 속한 분위의 개수 증가
        for (double profit : sortedProfits) {
            int index = (int) ((profit - minProfit) / range);
            if (index >= quantile) index = quantile - 1; // 마지막 범위 처리
            histogram.get(index)[2]++;
        }

        return histogram;
    }

    /**
     * 히스토그램을 swing을 통해 시각화합니다.
     *
     * @param histogram
     */
    public void showHistogram(List<double[]> histogram) {
        JFrame frame = new JFrame("Histogram");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);

        HistogramPanel panel = new HistogramPanel(histogram);
        frame.add(panel);

        frame.setVisible(true);
    }

    /**
     * 주어진 문제 ID에 대해 k번째로 큰 가중치를 반환합니다.
     *
     * 이 메서드는 특정 문제 ID에 해당하는 가중치 배열에서 k번째로 큰 값을 찾습니다.
     * 가중치 배열은 null 값이나 음수 값을 제외하고, 내림차순으로 정렬하여 상위 k번째 값을 반환합니다.
     *
     * @param problemId 문제 번호
     * @param k 상위 k번째 값을 찾기 위한 정수 (1 이상이어야 함)
     * @return k번째로 큰 가중치 값 (Double), 유효한 값이 없는 경우 null 반환
     */
    public Double FindTopKWeights(int problemId, int k) {
        Double[] weights = maps.getWeightMap().get(problemId);

        if (weights == null || weights.length == 0 || k <= 0) {
            return null; // 유효하지 않은 경우
        }

        TreeMap<Double, Integer> weightMap = new TreeMap<>(Collections.reverseOrder());
        for (Double weight : weights) {
            if (weight != null && weight >0) {
                weightMap.put(weight, weightMap.getOrDefault(weight, 0) + 1);
            }
        }

        int count = 0;
        for (Map.Entry<Double, Integer> entry : weightMap.entrySet()) {
            count += entry.getValue();
            if (count >= k) {
                return entry.getKey();
            }
        }

        return null; // k번째로 큰 값이 없는 경우
    }


    public static void main(String[] args) {
        MyService service = new MyService();
        int variableIndex = 10;
        int problemId = 5;
        double maxTime = 0.01d;
//        System.out.printf("총 걸린 시간: %.2f초%n", service.getTotalTime());
//        System.out.printf("변수 %d를 포함한 문제들의 평균 소요 시간: %.6f초%n",variableIndex,service.getAverageTimeForVariable(variableIndex));
//        System.out.printf("%.6f초 이내에 풀린 문제 번호: %s%n", maxTime, service.getProblemsSolvedWithinTime(maxTime));
//        System.out.printf("문제 번호 %d의 수익률: %.2f%n", problemId, service.getProfitByProblemId(problemId));

        // sort 수익률
        List<Double> sortedprofits = service.getSortedProfits();

        // 10분위 평균 수익률 계산
//        List<Double> decileAverages = service.calculateAveragesFromSortedList(sortedprofits, 10);
//        System.out.println("10분위별 평균 수익률:");
//        for (int i = 0; i < decileAverages.size(); i++) {
//            System.out.printf("하위 %d%% ~ %d%%: %.4f%n", i * 10, (i + 1) * 10, decileAverages.get(i));
//        }

        //히스토그램
        List<double[]> histogram = service.generateHistogram(sortedprofits, 20);
        System.out.printf("%d분위별 데이터:%n", 20);
        for (int i = 0; i < histogram.size(); i++) {
            double[] data = histogram.get(i);
            System.out.printf("분위 %d: %.6f ~ %.6f (개수: %d)%n", i + 1, data[0], data[1], (int) data[2]);
        }

        System.out.printf("문제 번호 %d에서 %d번째로 큰 가중치: %.10f%n", problemId, 3, service.FindTopKWeights(problemId, 3));

//        service.showHistogram(histogram);

        System.out.printf("총 걸린 시간: %.6f초%n", service.getTotalTime());
        System.out.printf("변수 %d를 포함한 문제들의 평균 소요 시간: %.6f초%n",variableIndex,service.getAverageTimeForVariable(variableIndex));
        System.out.printf("%.6f초 이내에 풀린 문제 번호: %s%n", maxTime, service.getProblemsSolvedWithinTime(maxTime));
        System.out.printf("문제 번호 %d의 수익률: %.6f%n", problemId, service.getProfitByProblemId(problemId));
        System.out.printf("전체 문제 중 풀린 문제의 비율: %.6f(%%)%n", service.getProblemSolvedPercentage());

        service.drawEfficientFrontier();
    }
}
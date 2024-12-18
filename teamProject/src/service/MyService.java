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
     * valueMap에서 정렬된 위험 리스트를 반환합니다.
     *
     * @return 정렬된 위험 리스트
     */
    public List<Double> getSortedRisks() {
        TreeMap<Double, Integer> riskFrequency = new TreeMap<>(); // 수익률 -> 빈도수

        // valueMap에서 위험 데이터를 TreeMap에 추가
        for (Map.Entry<Integer, Double[]> entry : maps.getValueMap().entrySet()) {
            Double[] values = entry.getValue();
            if (values != null && values.length >= 2) {
                double risk = values[0]; // 위험
                riskFrequency.put(risk, riskFrequency.getOrDefault(risk, 0) + 1);
            }
        }

        // TreeMap에서 정렬된 위험 데이터를 리스트로 변환
        List<Double> risks = new ArrayList<>();
        for (Map.Entry<Double, Integer> entry : riskFrequency.entrySet()) {
            double profit = entry.getKey();
            int frequency = entry.getValue();
            for (int i = 0; i < frequency; i++) {
                risks.add(profit);
            }
        }

        return risks;
    }

    /**
     * valueMap에서 수익률 값을 기준으로 정렬된 TreeMap을 반환합니다. (내림차순)
     * 동일한 수익률 값을 가진 문제 번호들은 Integer[]에 저장됩니다.
     *
     * @return 수익률 값을 기준으로 정렬된 TreeMap (key = 수익률, value = 문제 번호 배열)
     */
    public TreeMap<Double, Integer[]> getTreeMapSortedByProfitGroupedByProblemId() {
        TreeMap<Double, List<Integer>> tempTreeMap = new TreeMap<>(Collections.reverseOrder()); // 임시 TreeMap (List로 관리)

        // valueMap 데이터를 순회하며 수익률 기준으로 TreeMap에 데이터 추가
        for (Map.Entry<Integer, Double[]> entry : maps.getValueMap().entrySet()) {
            Double[] values = entry.getValue();
            if (values != null && values.length >= 2) {
                double profit = values[1]; // 수익률 값
                int problemId = entry.getKey(); // 문제 번호

                // 동일한 수익률에 대해 문제 번호를 List에 추가
                tempTreeMap.putIfAbsent(profit, new ArrayList<>());
                tempTreeMap.get(profit).add(problemId);
            }
        }

        // List<Integer>를 Integer[]로 변환하여 최종 TreeMap 생성
        TreeMap<Double, Integer[]> sortedTreeMap = new TreeMap<>();
        for (Map.Entry<Double, List<Integer>> entry : tempTreeMap.entrySet()) {
            Double profit = entry.getKey();
            List<Integer> problemIds = entry.getValue();
            sortedTreeMap.put(profit, problemIds.toArray(new Integer[0]));
        }

        return sortedTreeMap;
    }

    /**
     * valueMap에서 위험 값을 기준으로 정렬된 TreeMap을 반환합니다. (오름차순)
     * 동일한 위험 값을 가진 문제 번호들은 Integer[]에 저장됩니다.
     *
     * @return 위험 값을 기준으로 정렬된 TreeMap (key = 위험, value = 문제 번호 배열)
     */
    public TreeMap<Double, Integer[]> getTreeMapSortedByRiskGroupedByProblemId() {
        TreeMap<Double, List<Integer>> tempTreeMap = new TreeMap<>(); // 임시 TreeMap (List로 관리)

        // valueMap 데이터를 순회하며 위험 기준으로 TreeMap에 데이터 추가
        for (Map.Entry<Integer, Double[]> entry : maps.getValueMap().entrySet()) {
            Double[] values = entry.getValue();
            if (values != null && values.length >= 2) {
                double risk = values[0]; // 위험 값
                int problemId = entry.getKey(); // 문제 번호

                // 동일한 위험에 대해 문제 번호를 List에 추가
                tempTreeMap.putIfAbsent(risk, new ArrayList<>());
                tempTreeMap.get(risk).add(problemId);
            }
        }

        // List<Integer>를 Integer[]로 변환하여 최종 TreeMap 생성
        TreeMap<Double, Integer[]> sortedTreeMap = new TreeMap<>();
        for (Map.Entry<Double, List<Integer>> entry : tempTreeMap.entrySet()) {
            Double risk = entry.getKey();
            List<Integer> problemIds = entry.getValue();
            sortedTreeMap.put(risk, problemIds.toArray(new Integer[0]));
        }

        return sortedTreeMap;
    }

    /**
     * 위험 값이 가장 낮은 (수익률이 가장 높은) 상위 문제 번호를 가져옵니다.
     * 중복 위험 값이 포함된 경우에도 상위 문제 번호들을 모두 반환합니다.
     *
     * @param SortedMap 값을 기준으로 정렬된 TreeMap (key = 위험, value = 문제 번호 배열) (위험: 내림차순, 수익: 오름차순)
     * @param topN 가져올 상위 문제 개수
     * @return 상위 문제 번호 리스트
     */
    public List<Integer> getTopNProblemsBySortedTreeMap(TreeMap<Double, Integer[]> SortedMap, int topN) {
        List<Integer> result = new ArrayList<>();
        int count = 0;

        // 순회
        for (Map.Entry<Double, Integer[]> entry : SortedMap.entrySet()) {
            Integer[] problemIds = entry.getValue();

            // 문제 번호를 결과 리스트에 추가
            result.addAll(Arrays.asList(problemIds));
            count += problemIds.length;

            // 상위 N개 문제를 초과하면 루프 종료
            if (count >= topN) {
                break;
            }
        }

        return result;
    }

    /**
     * 수익률이 가장 높은 topN개의 문제 번호를 반환합니다.
     * 동일한 수익률 값이 포함된 경우에도 모든 문제 번호를 반환합니다.
     *
     * @param profitMap 수익률 값을 기준으로 정렬된 TreeMap (key = 수익률, value = 문제 번호 배열)
     * @param topN 가져올 상위 문제 개수
     * @return 상위 문제 번호 리스트
     */
    public List<Integer> getTopNProblemsByHighestProfit(TreeMap<Double, Integer[]> profitMap, int topN) {
        List<Integer> result = new ArrayList<>();
        int count = 0;

        // 수익률이 높은 순서로 순회 (내림차순)
        for (Map.Entry<Double, Integer[]> entry : profitMap.descendingMap().entrySet()) {
            Integer[] problemIds = entry.getValue();

            // 문제 번호를 결과 리스트에 추가
            result.addAll(Arrays.asList(problemIds));
            count += problemIds.length;

            // 상위 N개 문제를 초과하면 루프 종료
            if (count >= topN) {
                break;
            }
        }

        return result;
    }

    /**
     * 정렬된 리스트를 기반으로 지정된 분위 개수로 나누어 평균을 계산합니다.
     *
     * @param sortedList 정렬된 수익률 리스트
     * @param divisions 분위 개수 (예: 10, 20 등)
     * @return 분위별 평균 수익률 리스트
     */
    public List<Double> calculateAveragesFromSortedList(List<Double> sortedList, int divisions) {
        List<Double> averages = new ArrayList<>();
        int total = sortedList.size();
        int chunkSize = total / divisions; // 한 분위의 데이터 개수

        for (int i = 0; i < divisions; i++) {
            int start = i * chunkSize;
            int end = (i == divisions - 1) ? total : start + chunkSize; // 마지막 분위는 남은 데이터 포함

            List<Double> quantile = sortedList.subList(start, end);
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
     * 정렬된 리스트를 기반으로 분위별 범위를 나누고, 각 범위에 속하는 데이터의 개수를 계산합니다.
     *
     * @param sortedList 정렬된 리스트
     * @param quantile 분위 개수 (예: 10, 20 등)
     * @return 분위별 데이터 배열 (각 분위의 lowerBound, upperBound, count를 포함)
     */
    public List<double[]> generateHistogram(List<Double> sortedList, int quantile) {
        List<double[]> histogram = new ArrayList<>();

        // 리스트의 최소값과 최대값
        double minProfit = sortedList.get(0);
        double maxProfit = sortedList.get(sortedList.size() - 1);

        // 각 분위의 범위 크기
        double range = (maxProfit - minProfit) / quantile;

        // 분위별 데이터 초기화
        for (int i = 0; i < quantile; i++) {
            double lowerBound = minProfit + i * range;
            double upperBound = (i == quantile - 1) ? maxProfit : lowerBound + range;
            histogram.add(new double[]{lowerBound, upperBound, 0});
        }

        // 각 데이터가 속한 분위의 개수 증가
        for (double profit : sortedList) {
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
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // 창만 닫기
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
        // sort 수익률
        List<Double> sortedprofits = service.getSortedProfits();

        // sort 위험
        List<Double> sortedrisks = service.getSortedRisks();

        //수익률 히스토그램
        List<double[]> histogram_return = service.generateHistogram(sortedprofits, 20);
        System.out.printf("%d분위별 데이터:%n", 20);
        for (int i = 0; i < histogram_return.size(); i++) {
            double[] data = histogram_return.get(i);
            System.out.printf("분위 %d: %.6f ~ %.6f (개수: %d)%n", i + 1, data[0], data[1], (int) data[2]);
        }

        //위험 히스토그램
        List<double[]> histogram_risk = service.generateHistogram(sortedrisks, 20);
        System.out.printf("%d분위별 데이터:%n", 20);
        for (int i = 0; i < histogram_risk.size(); i++) {
            double[] data = histogram_risk.get(i);
            System.out.printf("분위 %d: %.6f ~ %.6f (개수: %d)%n", i + 1, data[0], data[1], (int) data[2]);
        }

        service.showHistogram(histogram_return);
        service.showHistogram(histogram_risk);

        System.out.printf("총 걸린 시간: %.6f초%n", service.getTotalTime());
        System.out.printf("변수 %d를 포함한 문제들의 평균 소요 시간: %.6f초%n",variableIndex,service.getAverageTimeForVariable(variableIndex));
        System.out.printf("%.6f초 이내에 풀린 문제 번호: %s%n", maxTime, service.getProblemsSolvedWithinTime(maxTime));
        System.out.printf("문제 번호 %d의 수익률: %.6f%n", problemId, service.getProfitByProblemId(problemId));
        System.out.printf("전체 문제 중 풀린 문제의 비율: %.6f(%%)%n", service.getProblemSolvedPercentage());
        System.out.printf("문제 번호 %d에서 %d번째로 큰 가중치: %.10f%n", problemId, 3, service.FindTopKWeights(problemId, 3));

        service.drawEfficientFrontier();
    }
}
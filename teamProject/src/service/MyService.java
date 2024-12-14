package service;

import myutil.Maps;
import myutil.Maps.StatusNode; // StatusNode 클래스 직접 사용

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
    
    public float getProblemSolved() {
    	
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

    public static void main(String[] args) {
        MyService service = new MyService();
        int variableIndex = 10;
        int problemId = 5;
        double maxTime = 0.01d;
        System.out.printf("총 걸린 시간: %.6f초%n", service.getTotalTime());
        System.out.printf("변수 %d를 포함한 문제들의 평균 소요 시간: %.6f초%n",variableIndex,service.getAverageTimeForVariable(variableIndex));
        System.out.printf("%.6f초 이내에 풀린 문제 번호: %s%n", maxTime, service.getProblemsSolvedWithinTime(maxTime));
        System.out.printf("문제 번호 %d의 수익률: %.6f%n", problemId, service.getProfitByProblemId(problemId));
        System.out.printf("전체 문제 중 풀린 문제의 비율: %.6f(%%)%n", service.getProblemSolved());
        service.drawEfficientFrontier();
    }
}
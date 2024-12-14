package service;

import myutil.Maps;
import myutil.Maps.StatusNode; // StatusNode 클래스 직접 사용

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

    public static void main(String[] args) {
        MyService service = new MyService();
        System.out.printf("총 걸린 시간: %.2f초%n", service.getTotalTime());
    }
}
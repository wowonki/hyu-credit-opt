package service;
import java.util.*;

public class ServiceRunner {
    public static void main(String[] args) {
    	System.out.println("\n========= 데이터 불러오는 중 =========");
        Scanner scanner = new Scanner(System.in);
        MyService service = new MyService();
        System.out.println("\n========= 불러오기 완료 =========");
        int K;

        while (true) {
            System.out.println("\n========= 서비스 메뉴 =========");
            System.out.println("\n-------- 문제 풀이 관련 --------");
            System.out.println("11. 특정 변수를 포함한 문제의 평균 문제풀이 소요시간 확인하기");
            System.out.println("12. 특정 시간 이내에 해결된 문제들 확인하기");
            System.out.println("13. 전체 문제 중 optimal 하게 풀린 문제 비율 확인하기");
            System.out.println("14. 문제를 푼 총 소요시간 확인하기");
            System.out.println("\n-------- 포트폴리오 성과 관련 --------");
            System.out.println("21. 특정 문제의 수익률 확인하기");
            System.out.println("22. K개의 높은 수익률 포트폴리오 보기");
            System.out.println("23. K개의 안전한 포트폴리오 보기");
            System.out.println("24. Return 히스토그램 그리기");
            System.out.println("25. Risk 히스토그램 그리기");
            System.out.println("26. 효율적 투자선 그리기");
            System.out.println("\n-------- 기타 --------");
            System.out.println("91. 특정 문제의 상위 K번째 가중치 확인하기");
            
            System.out.println("0. 종료");
            System.out.print("선택: ");
            int choice = scanner.nextInt();

            switch (choice) {
                case 11:
                    System.out.print("변수 인덱스 입력(0-2999): ");
                    int index = scanner.nextInt();
                    System.out.printf("평균 소요 시간: %.6f초%n", service.getAverageTimeForVariable(index));
                    break;
                case 12:
                    System.out.print("시간 제한 입력(초): ");
                    double time = scanner.nextDouble();
                    System.out.println("풀린 문제: " + service.getProblemsSolvedWithinTime(time));
                    break;
                case 13:
                    System.out.printf("풀린 문제 비율: %.2f%%%n", service.getProblemSolvedPercentage());
                    break;
                case 14:
                    System.out.printf("총 소요 시간: %.6f초%n", service.getTotalTime());
                    break;
                case 21:
                    System.out.print("문제 번호 입력(1-80000): ");
                    int problemId = scanner.nextInt();
                    System.out.printf("문제 %d 수익률: %.6f%n", problemId, service.getProfitByProblemId(problemId));
                    break;
                case 22:
                    System.out.print("K 입력: ");
                    K = scanner.nextInt();
                    TreeMap<Double, Integer[]> lowestRisk = service.getTreeMapSortedByProfitGroupedByProblemId();
                    List<Integer> topNRisk = service.getTopNProblemsBySortedTreeMap(lowestRisk, K);
                    System.out.println("높은 Return의 K개의 포트폴리오: " + topNRisk);
                    break;
                case 23:
                    System.out.print("K 입력: ");
                    K = scanner.nextInt();
                    TreeMap<Double, Integer[]> highestReturn = service.getTreeMapSortedByRiskGroupedByProblemId();
                    List<Integer> topNReturn = service.getTopNProblemsBySortedTreeMap(highestReturn, K);
                    System.out.println("낮은 Risk의 K개 포트폴리오: " + topNReturn);
                    break;
                case 24:
                    List<double[]> hist1 = service.generateHistogram(service.getSortedProfits(), 20);
                    service.showHistogram(hist1);
                    break;
                case 25:
                    List<double[]> hist2 = service.generateHistogram(service.getSortedRisks(), 20);
                    service.showHistogram(hist2);
                    break;
                case 26:
                    service.drawEfficientFrontier();
                    break;
                case 91:
                	System.out.print("문제 번호 입력(1-80000): ");
                    int pid = scanner.nextInt();
                    System.out.print("K값 입력: ");
                    int k = scanner.nextInt();
                    System.out.printf("문제 %d의 %d번째 가중치: %.10f%n", pid, k, service.FindTopKWeights(pid, k));
                    break;
                case 0:
                    System.out.println("프로그램을 종료합니다.");
                    System.exit(0);
                default:
                    System.out.println("잘못된 입력입니다.");
            }
        }
    }
}

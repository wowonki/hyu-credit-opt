package service;
import java.util.*;

public class ServiceRunner {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        MyService service = new MyService();

        while (true) {
            System.out.println("\n========= 서비스 메뉴 =========");
            System.out.println("1. 수익률 정렬");
            System.out.println("2. 위험 정렬");
            System.out.println("3. 수익률 히스토그램");
            System.out.println("4. 위험 히스토그램");
            System.out.println("5. 총 소요 시간 출력");
            System.out.println("6. 변수별 평균 소요 시간");
            System.out.println("7. 특정 시간 이내 문제 반환");
            System.out.println("8. 문제별 수익률 반환");
            System.out.println("9. optimal하게 풀린 문제 비율 반환");
            System.out.println("10. K번째 가중치 반환");
            System.out.println("11. 효율적 투자선 그리기");
            System.out.println("0. 종료");
            System.out.print("선택: ");
            int choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    System.out.println("수익률 정렬: " + service.getSortedProfits());
                    break;
                case 2:
                    System.out.println("위험 정렬: " + service.getSortedRisks());
                    break;
                case 3:
                    List<double[]> hist1 = service.generateHistogram(service.getSortedProfits(), 20);
                    service.showHistogram(hist1);
                    break;
                case 4:
                    List<double[]> hist2 = service.generateHistogram(service.getSortedRisks(), 20);
                    service.showHistogram(hist2);
                    break;
                case 5:
                    System.out.printf("총 소요 시간: %.6f초%n", service.getTotalTime());
                    break;
                case 6:
                    System.out.print("변수 인덱스 입력: ");
                    int index = scanner.nextInt();
                    System.out.printf("평균 소요 시간: %.6f초%n", service.getAverageTimeForVariable(index));
                    break;
                case 7:
                    System.out.print("시간 제한 입력: ");
                    double time = scanner.nextDouble();
                    System.out.println("풀린 문제: " + service.getProblemsSolvedWithinTime(time));
                    break;
                case 8:
                    System.out.print("문제 번호 입력: ");
                    int problemId = scanner.nextInt();
                    System.out.printf("문제 %d 수익률: %.6f%n", problemId, service.getProfitByProblemId(problemId));
                    break;
                case 9:
                    System.out.printf("풀린 문제 비율: %.2f%%%n", service.getProblemSolvedPercentage());
                    break;
                case 10:
                    System.out.print("문제 번호 입력: ");
                    int pid = scanner.nextInt();
                    System.out.print("K값 입력: ");
                    int k = scanner.nextInt();
                    System.out.printf("문제 %d의 %d번째 가중치: %.10f%n", pid, k, service.FindTopKWeights(pid, k));
                    break;
                case 11:
                    service.drawEfficientFrontier();
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

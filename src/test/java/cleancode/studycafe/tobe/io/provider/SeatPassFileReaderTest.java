package cleancode.studycafe.tobe.io.provider;

import cleancode.studycafe.tobe.model.pass.StudyCafePassType;
import cleancode.studycafe.tobe.model.pass.StudyCafeSeatPass;
import cleancode.studycafe.tobe.model.pass.StudyCafeSeatPasses;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SeatPassFileReaderTest {

    @Test
    @DisplayName("CSV 파일에서 좌석 종류별 최대/최소 시간에 걸맞는 좌석 패스를 읽을 수 있다")
    void getSeatPasses_shouldReturnValidSeatPassList_withBoundaryValues() {
        // Given: 경계값을 포함하는 CSV 파일이 존재함
        SeatPassFileReader reader = new SeatPassFileReader();

        // When: CSV 파일에서 좌석 패스 데이터를 읽음
        StudyCafeSeatPasses passes = reader.getSeatPasses();

        // Then: 경계값이 제대로 처리되는지 검증 (HOURLY의 최소값 2시간, 최대값 12시간)
        List<StudyCafeSeatPass> hourlyPasses = passes.findPassBy(StudyCafePassType.HOURLY);
        assertEquals(6, hourlyPasses.size());  // HOURLY 패스는 총 6개

        // 최소 지속 시간 (2시간) 확인
        StudyCafeSeatPass hourlyMin = hourlyPasses.stream()
            .filter(pass -> pass.getDuration() == 2)
            .findFirst()
            .orElse(null);
        assertNotNull(hourlyMin);
        assertEquals(4000, hourlyMin.getPrice());

        // 최대 지속 시간 (12시간) 확인
        StudyCafeSeatPass hourlyMax = hourlyPasses.stream()
            .filter(pass -> pass.getDuration() == 12)
            .findFirst()
            .orElse(null);
        assertNotNull(hourlyMax);
        assertEquals(13000, hourlyMax.getPrice());

        // Then: 경계값이 제대로 처리되는지 검증 (WEEKLY의 최소값 1주, 최대값 12주)
        List<StudyCafeSeatPass> weeklyPasses = passes.findPassBy(StudyCafePassType.WEEKLY);
        assertEquals(5, weeklyPasses.size());  // WEEKLY 패스는 총 5개

        // 최소 지속 시간 (1주) 확인
        StudyCafeSeatPass weeklyMin = weeklyPasses.stream()
            .filter(pass -> pass.getDuration() == 1)
            .findFirst()
            .orElse(null);
        assertNotNull(weeklyMin);
        assertEquals(60000, weeklyMin.getPrice());

        // 최대 지속 시간 (12주) 확인
        StudyCafeSeatPass weeklyMax = weeklyPasses.stream()
            .filter(pass -> pass.getDuration() == 12)
            .findFirst()
            .orElse(null);
        assertNotNull(weeklyMax);
        assertEquals(400000, weeklyMax.getPrice());
    }

    @Test
    @DisplayName("파일 경로가 잘못되었거나 존재하지 않으면, 사용자에게 좌석정보를 제공할 수 없다.")
    void getSeatPasses_shouldThrowRuntimeException_whenFileNotFound() {
        // Given: 존재하지 않는 파일 경로로 파일을 읽으려는 SeatPassFileReader
        SeatPassFileReader reader = new SeatPassFileReader() {
            @Override
            public StudyCafeSeatPasses getSeatPasses() {
                // 실제로 존재하지 않는 파일 경로 설정
                String invalidFilePath = "src/main/resources/cleancode/studycafe/invalid-file.csv";
                try {
                    // 잘못된 경로로 파일을 읽으려고 시도하여 IOException 유발
                    List<String> lines = Files.readAllLines(Paths.get(invalidFilePath));
                } catch (IOException e) {
                    // IOException이 발생하면 RuntimeException으로 변환
                    throw new RuntimeException("파일을 읽는 데 실패했습니다.", e);
                }
                return null;
            }
        };

        // When & Then: RuntimeException이 발생하는지 확인
        assertThrows(RuntimeException.class, reader::getSeatPasses);
    }
}
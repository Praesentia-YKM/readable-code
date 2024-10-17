package cleancode.studycafe.tobe.io;

import cleancode.studycafe.tobe.exception.AppException;
import cleancode.studycafe.tobe.model.pass.StudyCafePassType;
import cleancode.studycafe.tobe.model.pass.StudyCafeSeatPass;
import cleancode.studycafe.tobe.model.pass.StudyCafeSeatPasses;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class InputHandlerTest {

    @AfterEach
    void restoreSystemIn() {
        // 테스트가 끝난 후 System.in을 원래대로 복구
        System.setIn(System.in);
    }

    @Test
    @DisplayName("사용자가 1을 입력하면 HOURLY 타입을 반환한다")
    void getPassTypeSelectingUserAction_shouldReturnHourly_whenUserInputs1() {
        // Given
        String userInput = "1\n";
        System.setIn(new ByteArrayInputStream(userInput.getBytes()));

        InputHandler inputHandler = new InputHandler();

        // When
        StudyCafePassType passType = inputHandler.getPassTypeSelectingUserAction();

        // Then: HOURLY 타입이 반환되는지 확인
        assertThat(passType).isEqualTo(StudyCafePassType.HOURLY);
    }

    @Test
    @DisplayName("사용자가 잘못된 입력을 하면 예외가 발생한다")
    void getPassTypeSelectingUserAction_shouldThrowException_whenInvalidInput() {
        // Given
        InputHandler inputHandler = new InputHandler();
        String userInput = "invalid\n";
        System.setIn(new ByteArrayInputStream(userInput.getBytes()));

        // When & Then
        assertThatThrownBy(inputHandler::getPassTypeSelectingUserAction)
            .isInstanceOf(AppException.class)
            .hasMessage("잘못된 입력입니다.");
    }

    @Test
    @DisplayName("사용자가 1을 입력하면 해당 인덱스의 이용권을 반환한다")
    void getSelectPass_shouldReturnPass_whenValidInput() {
        // Given: 일급 컬렉션을 사용한 StudyCafeSeatPasses 생성
        InputHandler inputHandler = new InputHandler();
        StudyCafeSeatPasses passes = StudyCafeSeatPasses.of(List.of(StudyCafeSeatPass.of(StudyCafePassType.HOURLY, 1, 1000, 0.0), StudyCafeSeatPass.of(StudyCafePassType.WEEKLY, 1, 5000, 0.0)
        ));
        String userInput = "1\n"; // 첫 번째 이용권 선택
        System.setIn(new ByteArrayInputStream(userInput.getBytes()));

        // When
        StudyCafeSeatPass selectedPass = inputHandler.getSelectPass(passes.findPassBy(StudyCafePassType.HOURLY));

        // Then
        assertThat(selectedPass).isEqualTo(passes.findPassBy(StudyCafePassType.HOURLY).get(0));
    }

    @Test
    @DisplayName("사용자가 1을 입력하면 락커 사용을 선택한다")
    void getLockerSelection_shouldReturnTrue_whenUserInputs1() {
        // Given
        InputHandler inputHandler = new InputHandler();
        String userInput = "1\n";
        System.setIn(new ByteArrayInputStream(userInput.getBytes()));

        // When
        boolean lockerSelected = inputHandler.getLockerSelection();

        // Then
        assertThat(lockerSelected).isTrue();
    }

    @Test
    @DisplayName("사용자가 2를 입력하면 락커 사용을 선택하지 않는다")
    void getLockerSelection_shouldReturnFalse_whenUserInputs2() {
        // Given
        InputHandler inputHandler = new InputHandler();
        String userInput = "2\n";
        System.setIn(new ByteArrayInputStream(userInput.getBytes()));

        // When
        boolean lockerSelected = inputHandler.getLockerSelection();

        // Then
        assertThat(lockerSelected).isFalse();
    }
}

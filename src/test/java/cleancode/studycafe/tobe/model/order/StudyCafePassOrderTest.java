package cleancode.studycafe.tobe.model.order;

import cleancode.studycafe.tobe.model.pass.StudyCafePassType;
import cleancode.studycafe.tobe.model.pass.StudyCafeSeatPass;
import cleancode.studycafe.tobe.model.pass.locker.StudyCafeLockerPass;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class StudyCafePassOrderTest {

    @Test
    @DisplayName("Locker Pass가 없는 경우 총 가격과 할인 금액을 계산한다")
    void getTotalPrice_shouldReturnCorrectPrice_whenNoLockerPass() {
        // Given: HOURLY, 2 hours, 4000 price, no discount
        StudyCafeSeatPass seatPass = StudyCafeSeatPass.of(StudyCafePassType.HOURLY, 2, 4000, 0.0);
        StudyCafePassOrder order = StudyCafePassOrder.of(seatPass, null);

        // When
        int totalPrice = order.getTotalPrice();
        int discountPrice = order.getDiscountPrice();

        // Then
        assertThat(totalPrice).isEqualTo(4000);
        assertThat(discountPrice).isEqualTo(0);
        assertThat(order.getLockerPass()).isEmpty();
    }

    @Test
    @DisplayName("Locker Pass가 있는 경우 총 가격과 할인 금액을 계산한다")
    void getTotalPrice_shouldReturnCorrectPrice_whenLockerPassExists() {
        // Given: HOURLY, 2 hours, 4000 price, no discount for 좌석
        StudyCafeSeatPass seatPass = StudyCafeSeatPass.of(StudyCafePassType.HOURLY, 2, 4000, 0.0);
        // Locker Pass: HOURLY, 2 hours, 2000 price
        StudyCafeLockerPass lockerPass = StudyCafeLockerPass.of(StudyCafePassType.HOURLY, 2, 2000);
        StudyCafePassOrder order = StudyCafePassOrder.of(seatPass, lockerPass);

        // When
        int totalPrice = order.getTotalPrice();
        int discountPrice = order.getDiscountPrice();

        // Then
        assertThat(totalPrice).isEqualTo(6000);
        assertThat(discountPrice).isEqualTo(0);
        assertThat(order.getLockerPass()).isPresent();
    }

    @Test
    @DisplayName("Discount가 적용된 경우 총 가격과 할인 금액을 계산한다")
    void getTotalPrice_shouldApplyDiscount_whenSeatPassHasDiscount() {
        // Given: WEEKLY, 2 weeks, 100000 price, 10% discount
        StudyCafeSeatPass seatPass = StudyCafeSeatPass.of(StudyCafePassType.WEEKLY, 2, 100000, 0.1);
        StudyCafePassOrder order = StudyCafePassOrder.of(seatPass, null);

        // When
        int totalPrice = order.getTotalPrice();
        int discountPrice = order.getDiscountPrice();

        // Then
        assertThat(totalPrice).isEqualTo(90000);
        assertThat(discountPrice).isEqualTo(10000);
        assertThat(order.getLockerPass()).isEmpty();
    }

    @Test
    @DisplayName("Locker Pass가 있을 때 Optional로 반환되는 값을 확인한다")
    void getLockerPass_shouldReturnOptional_whenLockerPassIsPresent() {
        // Given: FIXED, 4 weeks, 250000 price, 10% discount
        StudyCafeSeatPass seatPass = StudyCafeSeatPass.of(StudyCafePassType.FIXED, 4, 250000, 0.1);
        // Locker Pass: FIXED, 4 weeks, 20000 price
        StudyCafeLockerPass lockerPass = StudyCafeLockerPass.of(StudyCafePassType.FIXED, 4, 20000);
        StudyCafePassOrder order = StudyCafePassOrder.of(seatPass, lockerPass);

        // When
        Optional<StudyCafeLockerPass> optionalLockerPass = order.getLockerPass();

        // Then
        assertThat(optionalLockerPass).isPresent();
        assertThat(optionalLockerPass.get().getPrice()).isEqualTo(20000);
    }
}

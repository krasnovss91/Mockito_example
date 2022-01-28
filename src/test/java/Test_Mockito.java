import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)//сообщаем, что используем mockito
public class Test_Mockito {

    @Mock
    ICalculator mcalc;//ставим заглушку (заместитель какого-то класса)

    @InjectMocks
    Calculator calculator = new Calculator(mcalc);//здесь создаём новый экземпляр класса-наследника нашей заглушки

    @Test
    public void testCalcAdd() {
        // определяем поведение калькулятора для операции сложения
        when(calculator.add(10.0, 20.0)).thenReturn(30.0);

        // проверяем действие сложения
        assertEquals(calculator.add(10, 20), 30.0, 0);
        // проверяем выполнение действия
        verify(mcalc).add(10.0, 20.0);

        // определение поведения с использованием doReturn
        doReturn(15.0).when(mcalc).add(10.0, 5.0);

        // проверяем действие сложения
        assertEquals(calculator.add(10.0, 5.0), 15.0, 0);
        verify(mcalc).add(10.0, 5.0);
    }

    @Test
    public void testCallMethod() {
        // определяем поведение (результаты)
        when(mcalc.subtract(15.0, 25.0)).thenReturn(10.0);
        when(mcalc.subtract(35.0, 25.0)).thenReturn(-10.0);

        // вызов метода subtract и проверка результата
        assertEquals(calculator.subtract(15.0, 25.0), 10, 0);
        assertEquals(calculator.subtract(15.0, 25.0), 10, 0);

        assertEquals(calculator.subtract(35.0, 25.0), -10, 0);

        // проверка вызова методов
        verify(mcalc, atLeastOnce()).subtract(35.0, 25.0);
        verify(mcalc, atLeast(2)).subtract(15.0, 25.0);

        // проверка - был ли метод вызван 2 раза?
        verify(mcalc, times(2)).subtract(15.0, 25.0);
        // проверка - метод не был вызван ни разу
        verify(mcalc, never()).divide(10.0, 20.0);

        /* Если снять комментарий со следующей проверки, то
         * ожидается exception, поскольку метод "subtract"
         * с параметрами (35.0, 25.0) был вызван 1 раз
         */
        // verify(mcalc, atLeast (2)).subtract(35.0, 25.0);

        /* Если снять комментарий со следующей проверки, то
         * ожидается exception, поскольку метод "subtract"
         * с параметрами (15.0, 25.0) был вызван 2 раза, а
         * ожидался всего один вызов
         */
        // verify(mcalc, atMost (1)).subtract(15.0, 25.0);
    }

    @Test
    public void testDevide() {
        when(mcalc.divide(15.0, 3)).thenReturn(5.0);

        assertEquals(calculator.divide(15.0, 3), 5.0, 0);
        // проверка вызова метода
        verify(mcalc).divide(15.0, 3);

        // создаем исключение
        RuntimeException exception = new RuntimeException("Division by zero");
        // определяем поведение
        doThrow(exception).when(mcalc).divide(15.0, 0);

        assertEquals(calculator.divide(15.0, 0), 0.0, 0);
        verify(mcalc).divide(15.0, 0);
    }

    // метод обработки ответа
    private Answer<Double> answer = new Answer<Double>() {
        @Override
        public Double answer(InvocationOnMock invocation) throws Throwable {
            // получение объекта mock
            Object mock = invocation.getMock();
            System.out.println("mock object : " + mock.toString());

            // аргументы метода, переданные mock
            Object[] args = invocation.getArguments();
            double d1 = (double) args[0];
            double d2 = (double) args[1];
            double d3 = d1 + d2;
            System.out.println("" + d1 + " + " + d2);

            return d3;
        }
    };

    @Test
    public void testThenAnswer() {
        // определение поведения mock для метода с параметрами
        when(mcalc.add(11.0, 12.0)).thenAnswer(answer);
        assertEquals(calculator.add(11.0, 12.0), 23.0, 0);
    }


}

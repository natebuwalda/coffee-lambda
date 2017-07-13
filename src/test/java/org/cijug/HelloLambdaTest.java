package org.cijug;


import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.assertEquals;

public class HelloLambdaTest {

    @Test
    public void shouldSayHello() {
        Context mockContext = Mockito.mock(Context.class);
        LambdaLogger mockLogger = Mockito.mock(LambdaLogger.class);
        HelloLambda lambda = new HelloLambda();

        Mockito.when(mockContext.getLogger()).thenReturn(mockLogger);

        String result = lambda.handleRequest("CIJUG", mockContext);

        assertEquals("Hello CIJUG", result);
    }

}

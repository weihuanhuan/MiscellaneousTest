package spring.json.validator;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

public class RequestBodyValidateBeanRequestBuilder {

    private final MockMvc mockMvc;

    RequestBodyValidateBeanRequestBuilder(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }

    public ResultActions executeRequest(String api, Object input, ResultMatcher resultMatcher) throws Exception {
        String objectToString = WebTestConfig.objectToString(input);

        return mockMvc.perform(post(api)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(objectToString))
                .andExpect(resultMatcher);
    }

}

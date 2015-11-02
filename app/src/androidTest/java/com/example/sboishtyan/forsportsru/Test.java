package com.example.sboishtyan.forsportsru;

import com.example.sboishtyan.forsportsru.data.json.model.ImageLinkResponse;

import junit.framework.TestCase;

public class Test extends TestCase {

    ImageLinkResponse response;
    ImageLinkResponse nullResponse;
    ImageLinkResponse nullResponseResults;
    String            urlForTest;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        urlForTest = "getAndSaveHtmlPage";
        nullResponse = new ImageLinkResponse();
        nullResponseResults = new ImageLinkResponse();
        nullResponseResults.responseData = new ImageLinkResponse.ResponseData();
        response = new ImageLinkResponse(urlForTest);
    }

    @Override
    protected void runTest() throws Throwable {
        super.runTest();
        testOptional();
    }

    public void testOptional() {
        assertEquals(false, nullResponse.getImageUrl().isPresent());
        assertEquals(false, nullResponseResults.getImageUrl().isPresent());
        assertEquals(urlForTest, response.getImageUrl().get());
    }
}

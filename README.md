#HTTP API for a comparison application. 

####General info
The app lets upload two part of the data (a key in a Json payload)
to compare them. Meaning that once a couple of proper POST* requests 
are called to both `/v1/diff/id/left` and `/v1/diff/id/right`, 
we have an opportunity to compare the values of the data 
via `/v1/diff/id`.

The example of a request is:
{
	"data": "dXQxMXh5MjM4MTMxMXk="
}
where the value of the data is an encoded Base64 string. 
The app decodes such a value and stores it in MongoDB as a document as per 
an id and data part. It delegates the control of data duplication to
a MongoDB compound sequence. Meaning that it does not allow uploading multiple
payload with the same id and data part.

*PUT endpoints are not currently implemented for the sake of simplicity. 

After comparing the difference between the values of the data,
the app responses with either `EQUAL` or `DIFF` statuses in accordance with
the following:
- `EQUAL` when the decoded values are the same;   
- `DIFF` with the information of lengths and offsets 
when they are different. This data might be streamed to a client
and has `application/stream+json` media type.
It's worth mentioning that NOT_EQUAL_SIZE is not on the table here,
cause the app uses the equals method of String.
  
####Justification to use strings to compare.
Although Base64 is designed to send some binary data, 
the app uses it accept encoded strings. This is because it's not so 
interesting and visual to compare binary data in terms of
offsets and lengths.
          
####Requirements to consider before using the app.
1. The project should be built by means of Gradle. For that reason, run `gradlew clean build`.
Subsequently, to start the application make use of `gradlew bootRun`
2. All the examples of possible requests might be found in  
`ComparisonApplicationIT` class including tests in accordance with basic commands.
3. Most part of the code is covered with unit tests based on Junit5 and Mockito2 as well. 


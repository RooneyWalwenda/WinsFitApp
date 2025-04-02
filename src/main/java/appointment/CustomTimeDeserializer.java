package appointment;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CustomTimeDeserializer extends JsonDeserializer<Time> {

    private SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    @Override
    public Time deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        String timeString = jsonParser.getText();
        try {
            Date parsedDate = dateFormat.parse(timeString);
            return new Time(parsedDate.getTime());
        } catch (ParseException e) {
            throw new IllegalArgumentException("Invalid time format: " + timeString);
        }
    }
}

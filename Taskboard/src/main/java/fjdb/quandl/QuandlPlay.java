package fjdb.quandl;

import com.jimmoores.quandl.DataSetRequest;
import com.jimmoores.quandl.TabularResult;
import com.jimmoores.quandl.classic.ClassicQuandlSession;
import org.threeten.bp.LocalDate;

/**
 * Created by francisbostock on 04/12/2017.
 */
public class QuandlPlay {

    public static void main(String[] args) {

        ClassicQuandlSession session = ClassicQuandlSession.create();
        LocalDate date = LocalDate.of(2017, 10, 1);
        TabularResult tabularResult = session.getDataSet(
//                DataSetRequest.Builder.of("WIKI/AAPL").withStartDate(date).build());
                DataSetRequest.Builder.of("LSE/ACC").withStartDate(date).build());

        /*for (int i = 0; i < tabularResult.size(); i++) {
            Row row = tabularResult.get(i);
            row.ge
        };*/

        System.out.println(tabularResult.toPrettyPrintedString());


    }
}

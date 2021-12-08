package pt.up.fe.els2021.combinators;

import org.junit.Assert;
import org.junit.Test;
import pt.up.fe.els2021.Table;

import java.util.List;

public class InnerJoinCombinatorTest {
    @Test
    public void simple() {
        var tableA = new Table(
                List.of("parentId", "parentName"),
                List.of(
                        List.of("1", "2"),
                        List.of("Mike", "Deborah")
                ));
        var tableB = new Table(
                List.of("childId", "parentId", "childName"),
                List.of(
                        List.of("1", "2", "3", "4"),
                        List.of("1", "2", "1", "3"),
                        List.of("Joe", "Maddie", "Lola", "Hans")
                ));

        var expectedTable = new Table(
                List.of("parentId", "parentName", "childId", "childName"),
                List.of(
                        List.of("1", "1", "2"),
                        List.of("Mike", "Mike", "Deborah"),
                        List.of("1", "3", "2"),
                        List.of("Joe", "Lola", "Maddie")));

        Assert.assertEquals(expectedTable, new InnerJoinCombinator("parentId").combine(tableA, tableB));
    }
}

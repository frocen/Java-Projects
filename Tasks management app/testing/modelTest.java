
import static org.junit.Assert.*;

import model.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.util.HashMap;

public class modelTest {
    private Task task;

    @Before
    public void setUp() {
        HashMap<Integer, CheckList> actionItems = new HashMap<Integer, CheckList>();
        task = new Task("task", "description", null, actionItems);
    }

    @After
    public void tearDown() {
        task = null;
    }


    @Test
    public void haveDueDate_false_dueDataIsNull() {
        assertEquals(task.haveDueDate(), false);
    }

    @Test
    public void haveDueDate_true_dueDataIsNotNull() {
        task.setDueDate(LocalDate.now());
        assertEquals(task.haveDueDate(), true);
    }

    @Test
    public void haveCheckList_false() {
        assertEquals(task.haveCheckList(), false);
    }

    @Test
    public void haveCheckList_true_addActionItem() {
        task.getActionItems().put(1, new CheckList("name", 0));
        assertEquals(task.haveCheckList(), true);
    }

    @Test
    public void completeNumber_isZero() {
        assertEquals(task.completeNumber(), 0);
    }

    @Test
    public void completeNumber_isOne_addCompleteItem() {
        task.getActionItems().put(1, new CheckList("name", 1));
        assertEquals(task.completeNumber(), 1);
    }

    @Test
    public void completePercentage_isOneHundredPercent_addCompleteItem() {
        task.getActionItems().put(1, new CheckList("name", 1));
        assertEquals(task.completePercentage(), 100.0, 0.0);
    }

    @Test
    public void completePercentage_isZeroPercent_addCompleteItem() {
        task.getActionItems().put(1, new CheckList("name", 0));
        assertEquals(task.completePercentage(), 0.0, 0.0);
    }

    @Test
    public void completePercentage_isFiftyPercent_addCompleteItem() {
        task.getActionItems().put(1, new CheckList("name", 1));
        task.getActionItems().put(2, new CheckList("name", 0));
        assertEquals(task.completePercentage(), 50.0, 0.0);
    }

    @Test
    public void isComplete_true_addActionItem() {
        task.getActionItems().put(1, new CheckList("name", 1));
        assertEquals(task.isComplete(), true);
    }

    @Test
    public void isComplete_false_addActionItem() {
        task.getActionItems().put(1, new CheckList("name", 0));
        assertEquals(task.isComplete(), false);
    }
}

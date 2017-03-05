package com.github.sah4ez.core.elements;

import com.github.sah4ez.core.data.*;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.ui.CustomTable;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Created by aleksandr on 06.01.17.
 */
public class CrossTableTest extends Assert {

    MyCrossTableTest crossTable;
    Logic logic = Mockito.mock(Logic.class);

    //<editor-fold desc="Containers for ELEMENT 1 and 4">
    DataContainer<Element1> element1DataContainer =
            new DataContainer<Element1>(Element1.class) {
                @Override
                protected void initHeaders() {
                    addCaption("id", "name", "price");
                    addHeader("ID", "NAME", "PRICE");
                    addCollapsed(true, false, false);
                }

                @Override
                public DataContainer loadAllData() {
                    this.add(new Element1(1, "name1", 1.0F));
                    this.add(new Element1(2, "name2", 2.0F));
                    this.add(new Element1(3, "name3", 3.0F));
                    return this;
                }
            };
    DataContainer<Element4> element4DataContainer =
            new DataContainer<Element4>(Element4.class) {
                @Override
                protected void initHeaders() {
                    addCaption("id", "name", "price");
                    addHeader("ID", "NAME", "PRICE");
                    addCollapsed(true, false, false);
                }

                @Override
                public DataContainer loadAllData() {
                    this.add(new Element4(1, "name1", 1.0F));
                    this.add(new Element4(2, "name2", 2.0F));
                    this.add(new Element4(3, "name3", 3.0F));
                    this.add(new Element4(4, "name4", 4.0F));
                    return this;
                }
            };
    //</editor-fold>

    @Before
    public void setUp() {
        crossTable = new MyCrossTableTest(logic,
                "crosstable1",
                element1DataContainer,
                element4DataContainer);
    }

    @Test
    public void testInnerContainer() {
        assertNotNull(crossTable.getFirstContainer());
        assertNotNull(crossTable.getSecondContainer());
        assertEquals(SelectionModeCrossTable.SINGLE_CELL, crossTable.getSelectionModeCrossTable());
    }

    @Test
    public void testSetContainer() {
        crossTable.setFirstContainer(null);
        crossTable.setSecondContainer(null);
        assertNull(crossTable.getFirstContainer());
        assertNull(crossTable.getSecondContainer());
        crossTable.setFirstContainer(element1DataContainer);
        crossTable.setSecondContainer(element4DataContainer);
        assertEquals(element1DataContainer, crossTable.getFirstContainer());
        assertEquals(element4DataContainer, crossTable.getSecondContainer());
    }

    @Test
    public void testCreatePropertyInCrossTable() {
        crossTable.createData("id", "name", "id", "name");
        assertEquals(6, crossTable.getTable().getContainerPropertyIds().size());
        Object[] property = crossTable.getTable().getContainerPropertyIds().toArray();
        assertEquals("id", property[0].toString());
        assertEquals("name", property[1].toString());
        assertEquals("1", property[2].toString());
        assertEquals("2", property[3].toString());
        assertEquals("3", property[4].toString());
        assertEquals("4", property[5].toString());
    }

    @Test
    public void testCreateRowsInCrossTable() throws NoSuchFieldException, IllegalAccessException {
        crossTable.createData("id", "name", "id", "name");
        assertEquals(3, crossTable.getTable().size());
        Object[] items = crossTable.getTable().getItemIds().toArray();

        assertEquals("1", getValueProperty(items[0], "id"));
        assertEquals("2", getValueProperty(items[1], "id"));
        assertEquals("3", getValueProperty(items[2], "id"));

        assertEquals("name1", getValueProperty(items[0], "name"));
        assertEquals("name2", getValueProperty(items[1], "name"));
        assertEquals("name3", getValueProperty(items[2], "name"));

        assertEquals(Condition.USE, getValueProperty(items[0], "1"));
        assertEquals(Condition.USE_EDIT, getValueProperty(items[0], "2"));
        assertEquals(Condition.USE_NOT_EDIT, getValueProperty(items[0], "3"));
        assertEquals(Condition.NOT_USE, getValueProperty(items[0], "4"));

        assertEquals(Condition.USE, getValueProperty(items[1], "1"));
        assertEquals(Condition.USE_EDIT, getValueProperty(items[1], "2"));
        assertEquals(Condition.USE_NOT_EDIT, getValueProperty(items[1], "3"));
        assertEquals(Condition.NOT_USE, getValueProperty(items[1], "4"));

        assertEquals(Condition.USE, getValueProperty(items[2], "1"));
        assertEquals(Condition.USE_EDIT, getValueProperty(items[2], "2"));
        assertEquals(Condition.USE_NOT_EDIT, getValueProperty(items[2], "3"));
        assertEquals(Condition.NOT_USE, getValueProperty(items[2], "4"));
    }

    private Object getValueProperty(Object object, String property) {
        return crossTable.getTable().getItem(object).getItemProperty(property).getValue();
    }

    private String getStyleCell(Object object, String property) {
        String result;

        CustomTable.CellStyleGenerator generator = crossTable.getTable().getCellStyleGenerator();

        if (generator == null)
            result = "GENERATOR IS NULL!!";
        else
            result = generator.getStyle(crossTable.getTable(), object, property);

        return result;
    }

    @Test
    public void testStyleNameForCell() {
        crossTable.createData("id", "name", "id", "name");

        Object[] items = crossTable.getTable().getItemIds().toArray();

        assertEquals("use", getStyleCell(items[0], "1"));
        assertEquals("use-edit", getStyleCell(items[0], "2"));
        assertEquals("use-not-edit", getStyleCell(items[0], "3"));
        assertEquals("not-use", getStyleCell(items[0], "4"));
        assertNull(getStyleCell(items[0], "id"));
        assertNull(getStyleCell(items[0], "name"));

        assertNull(getStyleCell(null, null));
        assertNull(getStyleCell(items[0], null));
        assertNull(getStyleCell(null, "1"));
    }

    @Test
    public void testSingleSelectModeTheSameCell() {
        ItemClickEvent itemClickEvent = Mockito.mock(ItemClickEvent.class);
        crossTable.createData("id", "name", "id", "name");
        Object[] items = crossTable.getTable().getItemIds().toArray();

        Mockito.when(itemClickEvent.getItem()).thenReturn(crossTable.getTable().getItem(items[0]));
        Mockito.when(itemClickEvent.getSource()).thenReturn(crossTable.getTable());
        Mockito.when(itemClickEvent.getPropertyId()).thenReturn("1");

        crossTable.setSelectionModeCrossTable(SelectionModeCrossTable.SINGLE_CELL);
        crossTable.actionSelectionMode(itemClickEvent);
        assertEquals("edit", getStyleCell(items[0], "1"));
        assertEquals("use", getStyleCell(items[1], "1"));

        crossTable.actionSelectionMode(itemClickEvent);

        assertEquals("use", getStyleCell(items[0], "1"));
        assertEquals("use", getStyleCell(items[1], "1"));
    }

    @Test
    public void testSingleSelectModeDifferentCell() {
        ItemClickEvent itemClickEvent = Mockito.mock(ItemClickEvent.class);
        crossTable.createData("id", "name", "id", "name");
        Object[] items = crossTable.getTable().getItemIds().toArray();

        Mockito.when(itemClickEvent.getItem())
                .thenReturn(crossTable.getTable().getItem(items[0]))
                .thenReturn(crossTable.getTable().getItem(items[1]))
                .thenReturn(crossTable.getTable().getItem(items[1]))
                .thenReturn(crossTable.getTable().getItem(items[0]));
        Mockito.when(itemClickEvent.getSource()).thenReturn(crossTable.getTable());
        Mockito.when(itemClickEvent.getPropertyId())
                .thenReturn("1")
                .thenReturn("2")
                .thenReturn("2")
                .thenReturn("1");

        crossTable.setSelectionModeCrossTable(SelectionModeCrossTable.SINGLE_CELL);

        crossTable.actionSelectionMode(itemClickEvent);

        assertEquals("edit", getStyleCell(items[0], "1"));
        assertEquals("use-edit", getStyleCell(items[1], "2"));

        crossTable.actionSelectionMode(itemClickEvent);

        assertEquals("use", getStyleCell(items[0], "1"));
        assertEquals("edit", getStyleCell(items[1], "2"));

        crossTable.actionSelectionMode(itemClickEvent);

        assertEquals("use", getStyleCell(items[0], "1"));
        assertEquals("use-edit", getStyleCell(items[1], "2"));

        crossTable.actionSelectionMode(itemClickEvent);

        assertEquals("edit", getStyleCell(items[0], "1"));
        assertEquals("use-edit", getStyleCell(items[1], "2"));
    }

    @Test
    public void testMultiCellInRowMode() {
        ItemClickEvent itemClickEvent = Mockito.mock(ItemClickEvent.class);
        crossTable.createData("id", "name", "id", "name");
        Object[] items = crossTable.getTable().getItemIds().toArray();

        Mockito.when(itemClickEvent.getItem())
                .thenReturn(crossTable.getTable().getItem(items[0]));
        Mockito.when(itemClickEvent.getSource()).thenReturn(crossTable.getTable());
        Mockito.when(itemClickEvent.getPropertyId())
                .thenReturn("1")
                .thenReturn("2")
                .thenReturn("4")
                .thenReturn("3")
                .thenReturn("2")
                .thenReturn("1")
                .thenReturn("4")
                .thenReturn("3");

        crossTable.setSelectionModeCrossTable(SelectionModeCrossTable.MULTI_CELL_IN_ROW);
        crossTable.actionSelectionMode(itemClickEvent);

        assertEquals("edit", getStyleCell(items[0], "1"));
        assertEquals("use-edit", getStyleCell(items[0], "2"));
        assertEquals("use-not-edit", getStyleCell(items[0], "3"));
        assertEquals("not-use", getStyleCell(items[0], "4"));

        crossTable.actionSelectionMode(itemClickEvent);

        assertEquals("edit", getStyleCell(items[0], "1"));
        assertEquals("edit", getStyleCell(items[0], "2"));
        assertEquals("use-not-edit", getStyleCell(items[0], "3"));
        assertEquals("not-use", getStyleCell(items[0], "4"));

        crossTable.actionSelectionMode(itemClickEvent);

        assertEquals("edit", getStyleCell(items[0], "1"));
        assertEquals("edit", getStyleCell(items[0], "2"));
        assertEquals("use-not-edit", getStyleCell(items[0], "3"));
        assertEquals("edit", getStyleCell(items[0], "4"));

        crossTable.actionSelectionMode(itemClickEvent);

        assertEquals("edit", getStyleCell(items[0], "1"));
        assertEquals("edit", getStyleCell(items[0], "2"));
        assertEquals("edit", getStyleCell(items[0], "3"));
        assertEquals("edit", getStyleCell(items[0], "4"));

        crossTable.actionSelectionMode(itemClickEvent);

        assertEquals("edit", getStyleCell(items[0], "1"));
        assertEquals("use-edit", getStyleCell(items[0], "2"));
        assertEquals("edit", getStyleCell(items[0], "3"));
        assertEquals("edit", getStyleCell(items[0], "4"));

        crossTable.actionSelectionMode(itemClickEvent);

        assertEquals("use", getStyleCell(items[0], "1"));
        assertEquals("use-edit", getStyleCell(items[0], "2"));
        assertEquals("edit", getStyleCell(items[0], "3"));
        assertEquals("edit", getStyleCell(items[0], "4"));

        crossTable.actionSelectionMode(itemClickEvent);

        assertEquals("use", getStyleCell(items[0], "1"));
        assertEquals("use-edit", getStyleCell(items[0], "2"));
        assertEquals("edit", getStyleCell(items[0], "3"));
        assertEquals("not-use", getStyleCell(items[0], "4"));

        crossTable.actionSelectionMode(itemClickEvent);

        assertEquals("use", getStyleCell(items[0], "1"));
        assertEquals("use-edit", getStyleCell(items[0], "2"));
        assertEquals("use-not-edit", getStyleCell(items[0], "3"));
        assertEquals("not-use", getStyleCell(items[0], "4"));
    }

    @Test
    public void testMultiCellInRowModeWithDifferentColumn(){
                ItemClickEvent itemClickEvent = Mockito.mock(ItemClickEvent.class);
        crossTable.createData("id", "name", "id", "name");
        Object[] items = crossTable.getTable().getItemIds().toArray();

        Mockito.when(itemClickEvent.getItem())
                .thenReturn(crossTable.getTable().getItem(items[0]))
                .thenReturn(crossTable.getTable().getItem(items[1]));
        Mockito.when(itemClickEvent.getSource()).thenReturn(crossTable.getTable());
        Mockito.when(itemClickEvent.getPropertyId())
                .thenReturn("1")
                .thenReturn("2");

        crossTable.setSelectionModeCrossTable(SelectionModeCrossTable.MULTI_CELL_IN_ROW);
        crossTable.actionSelectionMode(itemClickEvent);

        assertEquals("edit", getStyleCell(items[0], "1"));
        assertEquals("use-edit", getStyleCell(items[1], "2"));

        crossTable.actionSelectionMode(itemClickEvent);

        assertEquals("edit", getStyleCell(items[0], "1"));
        assertEquals("use-edit", getStyleCell(items[1], "2"));

    }

    @Test
    public void testMultiCellInColumnMode() {
        ItemClickEvent itemClickEvent = Mockito.mock(ItemClickEvent.class);
        crossTable.createData("id", "name", "id", "name");
        Object[] items = crossTable.getTable().getItemIds().toArray();

        Mockito.when(itemClickEvent.getItem())
                .thenReturn(crossTable.getTable().getItem(items[0]))
                .thenReturn(crossTable.getTable().getItem(items[1]))
                .thenReturn(crossTable.getTable().getItem(items[2]))
                .thenReturn(crossTable.getTable().getItem(items[1]))
                .thenReturn(crossTable.getTable().getItem(items[2]))
                .thenReturn(crossTable.getTable().getItem(items[0]));
        Mockito.when(itemClickEvent.getSource()).thenReturn(crossTable.getTable());
        Mockito.when(itemClickEvent.getPropertyId()).thenReturn("1");

        crossTable.setSelectionModeCrossTable(SelectionModeCrossTable.MULTI_CELL_IN_COLUMN);

        crossTable.actionSelectionMode(itemClickEvent);

        assertEquals("edit", getStyleCell(items[0], "1"));
        assertEquals("use", getStyleCell(items[1], "1"));
        assertEquals("use", getStyleCell(items[2], "1"));

        crossTable.actionSelectionMode(itemClickEvent);

        assertEquals("edit", getStyleCell(items[0], "1"));
        assertEquals("edit", getStyleCell(items[1], "1"));
        assertEquals("use", getStyleCell(items[2], "1"));

        crossTable.actionSelectionMode(itemClickEvent);

        assertEquals("edit", getStyleCell(items[0], "1"));
        assertEquals("edit", getStyleCell(items[1], "1"));
        assertEquals("edit", getStyleCell(items[2], "1"));

        crossTable.actionSelectionMode(itemClickEvent);

        assertEquals("edit", getStyleCell(items[0], "1"));
        assertEquals("use", getStyleCell(items[1], "1"));
        assertEquals("edit", getStyleCell(items[2], "1"));

        crossTable.actionSelectionMode(itemClickEvent);

        assertEquals("edit", getStyleCell(items[0], "1"));
        assertEquals("use", getStyleCell(items[1], "1"));
        assertEquals("use", getStyleCell(items[2], "1"));

        crossTable.actionSelectionMode(itemClickEvent);

        assertEquals("use", getStyleCell(items[0], "1"));
        assertEquals("use", getStyleCell(items[1], "1"));
        assertEquals("use", getStyleCell(items[2], "1"));
    }

    @Test
    public void testMultiCellInColumnModeWithDifferentRow(){
                        ItemClickEvent itemClickEvent = Mockito.mock(ItemClickEvent.class);
        crossTable.createData("id", "name", "id", "name");
        Object[] items = crossTable.getTable().getItemIds().toArray();

        Mockito.when(itemClickEvent.getItem())
                .thenReturn(crossTable.getTable().getItem(items[0]))
                .thenReturn(crossTable.getTable().getItem(items[1]));
        Mockito.when(itemClickEvent.getSource()).thenReturn(crossTable.getTable());
        Mockito.when(itemClickEvent.getPropertyId())
                .thenReturn("1")
                .thenReturn("2");

        crossTable.setSelectionModeCrossTable(SelectionModeCrossTable.MULTI_CELL_IN_COLUMN);
        crossTable.actionSelectionMode(itemClickEvent);

        assertEquals("edit", getStyleCell(items[0], "1"));
        assertEquals("use-edit", getStyleCell(items[1], "2"));

        crossTable.actionSelectionMode(itemClickEvent);

        assertEquals("edit", getStyleCell(items[0], "1"));
        assertEquals("use-edit", getStyleCell(items[1], "2"));

    }

    @Test
    public void testMultiCellMode(){
        ItemClickEvent itemClickEvent = Mockito.mock(ItemClickEvent.class);
        crossTable.createData("id", "name", "id", "name");
        Object[] items = crossTable.getTable().getItemIds().toArray();

        Mockito.when(itemClickEvent.getItem())
                .thenReturn(crossTable.getTable().getItem(items[0]))
                .thenReturn(crossTable.getTable().getItem(items[1]))
                .thenReturn(crossTable.getTable().getItem(items[1]))
                .thenReturn(crossTable.getTable().getItem(items[1]))
                .thenReturn(crossTable.getTable().getItem(items[0]));
        Mockito.when(itemClickEvent.getSource()).thenReturn(crossTable.getTable());
        Mockito.when(itemClickEvent.getPropertyId())
                .thenReturn("1")
                .thenReturn("2")
                .thenReturn("3")
                .thenReturn("2")
                .thenReturn("1");

        crossTable.setSelectionModeCrossTable(SelectionModeCrossTable.MULTI_CELL);

        crossTable.actionSelectionMode(itemClickEvent);

        assertEquals("edit", getStyleCell(items[0], "1"));
        assertEquals("use-edit", getStyleCell(items[1], "2"));
        assertEquals("use-not-edit", getStyleCell(items[1], "3"));

        crossTable.actionSelectionMode(itemClickEvent);

        assertEquals("edit", getStyleCell(items[0], "1"));
        assertEquals("edit", getStyleCell(items[1], "2"));
        assertEquals("use-not-edit", getStyleCell(items[1], "3"));

        crossTable.actionSelectionMode(itemClickEvent);

        assertEquals("edit", getStyleCell(items[0], "1"));
        assertEquals("edit", getStyleCell(items[1], "2"));
        assertEquals("edit", getStyleCell(items[1], "3"));

        crossTable.actionSelectionMode(itemClickEvent);

        assertEquals("edit", getStyleCell(items[0], "1"));
        assertEquals("use-edit", getStyleCell(items[1], "2"));
        assertEquals("edit", getStyleCell(items[1], "3"));

        crossTable.actionSelectionMode(itemClickEvent);

        assertEquals("use", getStyleCell(items[0], "1"));
        assertEquals("use-edit", getStyleCell(items[1], "2"));
        assertEquals("edit", getStyleCell(items[1], "3"));
    }

    @Test
    public void testChangeConditionSelectedCell(){

        ItemClickEvent itemClickEvent = Mockito.mock(ItemClickEvent.class);
        crossTable.createData("id", "name", "id", "name");
        Object[] items = crossTable.getTable().getItemIds().toArray();

        Mockito.when(itemClickEvent.getItem())
                .thenReturn(crossTable.getTable().getItem(items[0]))
                .thenReturn(crossTable.getTable().getItem(items[1]));
        Mockito.when(itemClickEvent.getSource()).thenReturn(crossTable.getTable());
        Mockito.when(itemClickEvent.getPropertyId()).thenReturn("1");

        crossTable.setSelectionModeCrossTable(SelectionModeCrossTable.SINGLE_CELL);

        crossTable.actionSelectionMode(itemClickEvent);

        assertEquals("edit", getStyleCell(items[0], "1"));
        assertEquals("use", getStyleCell(items[1], "1"));

        crossTable.changeSelectedCellCondition(Condition.USE_EDIT);

        crossTable.actionSelectionMode(itemClickEvent);

        assertEquals("use-edit", getStyleCell(items[0], "1"));
        assertEquals("edit", getStyleCell(items[1], "1"));

    }

    @Test
    public void testClearLayout(){
        crossTable.clearLayout();
    }

    private class MyCrossTableTest extends CrossTable {

        public MyCrossTableTest(Logic logic, String identify, DataContainer<? extends Entity> first, DataContainer<? extends Entity> second) {
            super(logic, identify, first, second);
        }

        @Override
        public CellCondition getCell(Object idRow, Object idColumn) {
            if (idColumn instanceof String) {
                switch ((String) idColumn) {
                    case "1":
                        return Condition.USE;
                    case "2":
                        return Condition.USE_EDIT;
                    case "3":
                        return Condition.USE_NOT_EDIT;
                    case "4":
                        return Condition.NOT_USE;
                }
            }
            return Condition.USE;
        }

        @Override
        protected ItemClickEvent.ItemClickListener editTableItemClick() {
            return itemClickEvent -> {
            };
        }

        @Override
        protected ItemClickEvent.ItemClickListener selectTableItemClick() {
            return itemClickEvent -> {
            };
        }

        @Override
        protected ItemClickEvent.ItemClickListener editTableAllItemClick() {
            return itemClickEvent -> {
            };
        }

        @Override
        protected ItemClickEvent.ItemClickListener selectTableAllItemClick() {
            return itemClickEvent -> {
            };
        }

        @Override
        public void clearLayout() {

        }
    }

    //<editor-fold desc="Description ENTITY CLASS">
    private class Element1 extends CommonEntity {
        private Integer id = 0;
        private String name = "";
        private Float price = 0.0F;
        private Condition condition = Condition.USE;

        private DataContainer<Element2> element2DataContainer;
        private DataContainer<Element3> element3DataContainer;

        public Element1(Integer id, String name, Float price) {
            this.id = id;
            this.name = name;
            this.price = price;
            element2DataContainer =
                    new DataContainer<Element2>(Element2.class) {
                        @Override
                        protected void initHeaders() {
                            addCaption("id", "name", "price");
                            addHeader("ID", "NAME", "PRICE");
                            addCollapsed(true, false, false);
                        }

                        @Override
                        public DataContainer loadAllData() {
                            this.add(new Element2(1, "name1", 1.0F));
                            this.add(new Element2(2, "name2", 2.0F));
                            this.add(new Element2(3, "name3", 3.0F));
                            this.add(new Element2(4, "name4", 4.0F));
                            return this;
                        }
                    };
            element3DataContainer =
                    new DataContainer<Element3>(Element3.class) {
                        @Override
                        protected void initHeaders() {
                            addCaption("id", "name", "price");
                            addHeader("ID", "NAME", "PRICE");
                            addCollapsed(true, false, false);
                        }

                        @Override
                        public DataContainer loadAllData() {
                            this.add(new Element3(1, "name1", 1.0F));
                            this.add(new Element3(2, "name2", 2.0F));
                            this.add(new Element3(3, "name3", 3.0F));
                            this.add(new Element3(4, "name4", 4.0F));
                            return this;
                        }
                    };
        }
    }

    private class Element2 extends CommonEntity {
        private Integer id = 0;
        private String name = "";
        private Float price = 0.0F;

        public Element2(Integer id, String name, Float price) {
            this.id = id;
            this.name = name;
            this.price = price;
        }
    }

    private class Element3 extends CommonEntity {
        private Integer id = 0;
        private String name = "";
        private Float price = 0.0F;

        public Element3(Integer id, String name, Float price) {
            this.id = id;
            this.name = name;
            this.price = price;
        }
    }

    private class Element4 extends CommonEntity {
        private Integer id = 0;
        private String name = "";
        private Float price = 0.0F;

        public Element4(Integer id, String name, Float price) {
            this.id = id;
            this.name = name;
            this.price = price;
        }
    }
    //</editor-fold>
}

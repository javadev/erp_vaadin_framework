package com.github.sah4ez.core.elements;

import com.github.sah4ez.core.permission.ModifierAccess;
import com.vaadin.server.Resource;
import com.vaadin.ui.Label;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Created by aleksandr on 28.12.16.
 */
public class BottomTabsTest extends Assert {

    TestTabs tabs;
    Logic logic = Mockito.mock(Logic.class);
    Workspace test = Mockito.mock(Workspace.class);

    @Before
    public void setUp() throws Exception {
        tabs = new TestTabs(logic, "tabId");
    }

    @Test
    public void setIdentify() throws Exception {
        tabs.setIdentify("tabId1");
        assertEquals("tabId1", tabs.getIdentify());
    }

    @Test
    public void getIdentify() throws Exception {
        assertEquals("tabId", tabs.getIdentify());
    }

    @Test
    public void addCaption() throws Exception {
        tabs.addCaption("tab1", "tab2");
        assertEquals(4, tabs.getCaptions().size());
    }

    @Test
    public void addComponent() throws Exception {
        tabs.addComponent(new Label("1"), new Label("2"));
        assertEquals(4, tabs.getComponents().size());
    }


    @Test
    public void addResource() throws Exception {
        Resource r1 = Mockito.mock(Resource.class);
        Resource r2 = Mockito.mock(Resource.class);

        tabs.addResource(r1, r2);
        assertEquals(4, tabs.getResources().size());
    }


    @Test
    public void initTabs() throws Exception {
        tabs.initTabs();
        assertEquals(4, tabs.getCaptions().size());
        assertEquals(4, tabs.getComponents().size());
        assertEquals(4, tabs.getResources().size());

    }

    @Test
    public void getSelectedTabIndex() throws Exception {
        assertEquals(0, tabs.getSelectedTabIndex());
    }

    @Test
    public void setModifiersAccess() throws Exception {
        tabs.addComponent(test);
        tabs.setModifierAccess(ModifierAccess.EDIT);
        assertEquals(ModifierAccess.EDIT, tabs.getModifierAccess());
        Mockito.verify(test).replacePermissionAccess(ModifierAccess.EDIT);
    }

    @Test
    public void replacePermissionAccess() throws Exception {
        assertEquals(ModifierAccess.HIDE, tabs.getModifierAccess());
        tabs.replacePermissionAccess(ModifierAccess.EDIT);
        assertEquals(ModifierAccess.EDIT, tabs.getModifierAccess());
        tabs.replacePermissionAccess(ModifierAccess.HIDE);
        assertEquals(ModifierAccess.EDIT, tabs.getModifierAccess());
    }

    @Test
    public void getModifierAccess() throws Exception {
        assertEquals(ModifierAccess.HIDE, tabs.getModifierAccess());
    }

    @Test
    public void getLogic() throws Exception {
        assertNotNull(tabs.getLogic());
    }

    @Test
    public void clearTest() throws Exception{
        tabs.clear();
        assertEquals("", ((Label) tabs.getComponents().get(0)).getValue());
        assertEquals("", ((Label) tabs.getComponents().get(1)).getValue());

    }

    private class TestTabs extends BottomTabs{

        public TestTabs(Logic logic, String identify) {
            super(logic, identify);
        }

        @Override
        public void initTabs() {
            addCaption("tab1", "tab2");
            addComponent(new Label("1"), new Label("2"));
            addResource(null, null);
        }

        @Override
        public void clear() {
            getComponents().forEach( component -> ((Label) component).setValue(""));
        }
    }


}
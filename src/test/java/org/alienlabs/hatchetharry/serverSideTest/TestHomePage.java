package org.alienlabs.hatchetharry.serverSideTest;

import java.util.ArrayList;
import java.util.List;

import org.alienlabs.hatchetharry.HatchetHarryApplication;
import org.alienlabs.hatchetharry.view.component.ClockPanel;
import org.alienlabs.hatchetharry.view.component.DataBox;
import org.alienlabs.hatchetharry.view.component.HandComponent;
import org.alienlabs.hatchetharry.view.page.HomePage;
import org.apache.wicket.Component.IVisitor;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.spring.injection.annot.SpringComponentInjector;
import org.apache.wicket.util.tester.TagTester;
import org.apache.wicket.util.tester.WicketTester;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Simple test using the WicketTester
 */
public class TestHomePage
{

    private static WicketTester tester;
    private static HatchetHarryApplication webApp;
    List<Image> img;

    @Before
    public void setUp()
    {
        TestHomePage.webApp = new HatchetHarryApplication()
        {
            // note in this case the application context is in the default
            // package
            ApplicationContext context = new ClassPathXmlApplicationContext(
                    new String[] { "applicationContext.xml" });

            @Override
            public void init()
            {
                this.addComponentInstantiationListener(new SpringComponentInjector(this,
                        this.context, true));
            }
        };

        TestHomePage.tester = new WicketTester(TestHomePage.webApp);
        final ApplicationContext context = new ClassPathXmlApplicationContext(
                new String[] { "applicationContext.xml" });
        TestHomePage.tester.getApplication().addComponentInstantiationListener(
                new SpringComponentInjector(TestHomePage.tester.getApplication(), context, true));
    }


    @Test
    public void testRenderMyPage()
    {
        // start and render the test page
        TestHomePage.tester.startPage(HomePage.class);

        // assert rendered page class
        TestHomePage.tester.assertRenderedPage(HomePage.class);

        // assert rendered label component
        final Label message = (Label)TestHomePage.tester
                .getComponentFromLastRenderedPage("message");
        Assert.assertTrue(message.getDefaultModelObjectAsString().contains("version"));
        Assert.assertTrue(message.getDefaultModelObjectAsString().contains("release"));

    }

    @Test
    public void testRenderHands()
    {
        // start and render the test page
        TestHomePage.tester.startPage(HomePage.class);

        // assert rendered page class
        TestHomePage.tester.assertRenderedPage(HomePage.class);

        // assert hand is present
        TestHomePage.tester.assertComponent("handCardsPlaceholder:gallery", HandComponent.class);

        // assert hand content
        final HandComponent gallery = (HandComponent)TestHomePage.tester
                .getComponentFromLastRenderedPage("handCardsPlaceholder:gallery");

        this.img = new ArrayList<Image>();
        @SuppressWarnings("unchecked")
        final List<Image> images =
        (List<Image>)gallery.visitChildren(Image.class,
                new IVisitor<Image>()
                {
            @Override
            public List<Image> component(final Image component)
            {
                TestHomePage.this.img.add(component);
                return TestHomePage.this.img;
            }
                });

        // assert 1 image
        Assert.assertNotNull(images.get(0));
        Assert.assertEquals(1, images.size());

        // assert URL of a thumbnail
        List<TagTester> tagTester =
                TagTester.createTagsByAttribute(TestHomePage.tester
                        .getServletResponse().getDocument(), "class", "nav-thumb", false);
        Assert.assertNotNull(tagTester);
        Assert.assertNotNull(tagTester.get(0).getAttribute("src"));
        Assert.assertTrue(tagTester.get(0).getAttribute("src").contains(".jpg"));

        // assert number of thumbnails
        Assert.assertEquals(7, tagTester.size());
    }

    @Test
    public void testRenderClock()
    {
        // start and render the test page
        TestHomePage.tester.startPage(HomePage.class);

        // assert rendered page class
        TestHomePage.tester.assertRenderedPage(HomePage.class);

        // assert clock is present
        TestHomePage.tester.assertComponent("clockPanel", ClockPanel.class);

        // assert clock content
        final ClockPanel clock = (ClockPanel)TestHomePage.tester
                .getComponentFromLastRenderedPage("clockPanel");
        System.out.println("###" + clock.getTime().getObject());
        Assert.assertTrue(clock.getTime().getObject().contains("###"));
    }

    @Test
    public void testRenderMenuBar()
    {
        // start and render the test page
        TestHomePage.tester.startPage(HomePage.class);

        // assert rendered page class
        TestHomePage.tester.assertRenderedPage(HomePage.class);

        // Assert menubar
        List<TagTester> tagTester =
                TagTester.createTagsByAttribute(TestHomePage.tester
                        .getServletResponse().getDocument(), "class", "rootVoices", false);
        Assert.assertNotNull(tagTester);
        Assert.assertEquals(1, tagTester.size());

        // Assert menu entries
        tagTester =
                TagTester.createTagsByAttribute(TestHomePage.tester
                        .getServletResponse().getDocument(), "class", "mbmenu", false);
        Assert.assertNotNull(tagTester);
        Assert.assertTrue(tagTester.size() > 1);

        // Assert 'the game wins' entry exists
        boolean containsText = false;
        for (TagTester tt : tagTester) {
            if ((null != tt.getMarkup() && tt.getMarkup().contains("The game wins")))  {
                containsText = true;
                break;
            }
        }
        Assert.assertTrue(containsText);
    }

    @Test
    public void testRenderDataBox()
    {
        // start and render the test page
        TestHomePage.tester.startPage(HomePage.class);

        // assert rendered page class
        TestHomePage.tester.assertRenderedPage(HomePage.class);

        // assert DataBox is present
        TestHomePage.tester.assertComponent("dataBoxParent:dataBox", DataBox.class);
        
        // assert DataBox content
        TestHomePage.tester.assertLabel("dataBoxParent:dataBox:box:0:playerLifePoints", "20 life points");
    }
    
}

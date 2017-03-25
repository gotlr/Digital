package de.neemann.digital.gui;

import de.neemann.digital.draw.elements.VisualElement;
import de.neemann.digital.draw.graphics.Vector;
import de.neemann.digital.draw.library.ElementLibrary;
import de.neemann.digital.draw.library.LibraryNode;
import de.neemann.digital.draw.shapes.ShapeFactory;
import de.neemann.digital.gui.components.CircuitComponent;
import de.neemann.digital.lang.Lang;
import de.neemann.gui.ErrorMessage;
import de.neemann.gui.ToolTipAction;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.IOException;

/**
 * Action to insert the given node to the given circuit
 * Created by hneemann on 25.03.17.
 */
final class InsertAction extends ToolTipAction {
    private final LibraryNode node;
    private final InsertHistory insertHistory;
    private final CircuitComponent circuitComponent;
    private final ShapeFactory shapeFactory;

    /**
     * Creates a new instance
     *
     * @param node             the node which holds the element to add
     * @param insertHistory    the history to add the element to
     * @param circuitComponent the component to add the element to
     * @param shapeFactory     the shapeFactory to create the icon
     */
    public InsertAction(LibraryNode node, InsertHistory insertHistory, CircuitComponent circuitComponent, ShapeFactory shapeFactory) {
        super(node.getTranslatedName(), createIcon(node, shapeFactory));
        this.shapeFactory = shapeFactory;
        this.node = node;
        this.insertHistory = insertHistory;
        this.circuitComponent = circuitComponent;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        VisualElement visualElement = new VisualElement(node.getName()).setPos(new Vector(10, 10)).setShapeFactory(shapeFactory);
        circuitComponent.setPartToInsert(visualElement);
        if (getIcon() == null) {
            try {
                node.getDescription();
                setIcon(createIcon(node, shapeFactory));
            } catch (IOException ex) {
                SwingUtilities.invokeLater(new ErrorMessage(Lang.get("msg_errorImportingModel")).addCause(ex));
            }
        }
        insertHistory.add(this);
    }

    /**
     * @return true if element to insert is a custom element
     */
    public boolean isCustom() {
        return node.getDescriptionOrNull() instanceof ElementLibrary.ElementTypeDescriptionCustom;
    }

    private static ImageIcon createIcon(LibraryNode node, ShapeFactory shapeFactory) {
        // doesn't load the description if only the icon is needed
        // create action without an icon instead
        if (node.isDescriptionLoaded()) {
            try {
                return new VisualElement(node.getDescription().getName()).setShapeFactory(shapeFactory).createIcon(75);
            } catch (IOException ex) {
                SwingUtilities.invokeLater(new ErrorMessage(Lang.get("msg_errorImportingModel")).addCause(ex));
            }
        }
        return null;
    }

}
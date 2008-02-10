package org.basex.gui.view.query;

import static org.basex.Text.*;
import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.border.EmptyBorder;
import org.basex.gui.GUI;
import org.basex.gui.GUICommands;
import org.basex.gui.GUIConstants;
import org.basex.gui.GUIProp;
import org.basex.gui.layout.BaseXButton;
import org.basex.gui.layout.BaseXCheckBox;
import org.basex.gui.layout.BaseXLayout;
import org.basex.gui.view.View;
import org.basex.util.Token;

/**
 * This view allows the input of database queries.
 *
 * @author Workgroup DBIS, University of Konstanz 2005-08, ISC License
 * @author Christian Gruen
 */
public final class QueryView extends View {
  /** Input mode panels. */
  private static final int NPANELS = SEARCHMODE.length;
  /** Input mode panels. */
  QueryPanel[] panels = new QueryPanel[NPANELS];
  /** Input mode. */
  int mode;

  /** Input mode buttons. */
  private final BaseXButton[] input = new BaseXButton[NPANELS];
  /** Filter checkbox. */
  private BaseXCheckBox filterbox;
  /** Query panel. */
  private QueryPanel search;
  /** Button box. */
  private final Box box;

  /**
   * Default constructor.
   * @param help help text
   */
  public QueryView(final byte[] help) {
    super(help);
    setLayout(new BorderLayout(0, 4));
    setBorder(38, 8, 8, 8);

    box = new Box(BoxLayout.X_AXIS);
    box.setBorder(new EmptyBorder(0, 0, 8, 0));
    for(int i = 0; i < NPANELS; i++) {
      input[i] = new BaseXButton(SEARCHMODE[i], HELPSEARCH[i]);
      input[i].setActionCommand(Integer.toString(i));
      input[i].addActionListener(new ActionListener() {
        public void actionPerformed(final ActionEvent e) {
          mode = Integer.parseInt(e.getActionCommand());
          refreshLayout();
        }
      });
      box.add(input[i]);
      box.add(Box.createHorizontalStrut(6));
      if(i == 0) {
        panels[i] = new QueryArea(this);
      } else if(i == 1) {
        panels[i] = new QuerySimple(this);
      }
    }
    box.add(Box.createHorizontalStrut(6));

    final Box box2 = new Box(BoxLayout.Y_AXIS);
    filterbox = new BaseXCheckBox(CMDFILTERRT, HELPFILTERRT, GUIProp.filterrt);
    filterbox.addActionListener(new ActionListener() {
      public void actionPerformed(final ActionEvent e) {
        GUICommands.RTFILTER.execute();
      }
    });
    filterbox.setToolTipText(Token.string(HELPFILTERRT));
    box2.add(filterbox);
    box.add(box2);

    refresh();
  }

  @Override
  public void refreshInit() {
    if(!GUI.context.db()) {
      for(int i = 0; i < NPANELS; i++) panels[i].finish();
    } else {
      refreshLayout();
    }
  }

  @Override
  public void refreshFocus() { }

  @Override
  public void refreshMark() {
    if(search != null) search.refresh();
  }

  @Override
  public void refreshContext(final boolean more, final boolean quick) { }

  @Override
  public void refreshUpdate() {
    refreshLayout();
  }

  @Override
  public void refreshLayout() {
    for(int i = 0; i < NPANELS; i++)
      BaseXLayout.select(input[i], mode == i);

    removeAll();
    final int h = GUIConstants.lfont.getSize() + 24;
    setBorder(new EmptyBorder(h, 8, 8, 8));
    add(box, BorderLayout.NORTH);
    search = panels[mode];
    search.init();
    revalidate();
    repaint();
    search.query(true);
    filterbox.setSelected(GUIProp.filterrt);
    filterbox.setEnabled(mode == 1);
  }

  /**
   * Notifies all panels of the GUI termination.
   */
  public void quit() {
    for(final QueryPanel p : panels) p.quit();
  }

  /**
   * Refreshes the panel components.
   */
  void refresh() {
    BaseXLayout.select(input[mode], true);
    BaseXLayout.select(filterbox, GUIProp.filterrt);
  }

  @Override
  public void keyPressed(final KeyEvent e) {
    if(e.isAltDown()) super.keyPressed(e);
  }

  @Override
  public void keyTyped(final KeyEvent e) { }

  @Override
  public void paintComponent(final Graphics g) {
    super.paintComponent(g);
    BaseXLayout.antiAlias(g);

    final int ys = GUIConstants.lfont.getSize() + 5;
    g.setColor(GUIConstants.COLORS[16]);
    g.setFont(GUIConstants.lfont);
    g.drawString(GUIConstants.QUERYVIEW, 8, ys);

    if(search != null) search.refresh();
  }

  /**
   * Handles info messages resulting from a query execution.
   * @param info info message
   * @param ok true if query was successful
   * @return true if info was processed
   */
  public boolean info(final String info, final boolean ok) {
    if(search != null) search.info(info, ok);
    return search != null && mode != 1;
  }

  /**
   * Sets a new XQuery request.
   * @param xq XQuery
   */
  public void setXQuery(final byte[] xq) {
    panels[0].last = Token.string(xq);
    mode = 0;
    refreshLayout();
  }

  /**
   * Returns the last XQuery input..
   * @return XQuery
   */
  public byte[] getXQuery() {
    return Token.token(panels[0].last);
  }
}

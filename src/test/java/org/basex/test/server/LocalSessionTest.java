package org.basex.test.server;

import org.basex.core.*;
import org.basex.server.*;
import org.junit.*;

/**
 * This class tests the local API.
 *
 * @author BaseX Team 2005-12, BSD License
 * @author Christian Gruen
 */
public class LocalSessionTest extends SessionTest {
  /** Database context. */
  private static Context context;

  /** Starts the test. */
  @BeforeClass
  public static void startContext() {
    context = new Context();
  }

  /** Stops the test. */
  @AfterClass
  public static void stopContext() {
    context.close();
  }

  /** Starts a session. */
  @Before
  public void startSession() {
    session = new LocalSession(context, out);
  }
}

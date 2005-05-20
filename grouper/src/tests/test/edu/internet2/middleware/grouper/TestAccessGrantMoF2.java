/*
 * Copyright (C) 2004-2005 University Corporation for Advanced Internet Development, Inc.
 * Copyright (C) 2004-2005 The University Of Chicago
 * All Rights Reserved. 
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 *  * Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *  * Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *  * Neither the name of the University of Chicago nor the names
 *    of its contributors nor the University Corporation for Advanced
 *   Internet Development, Inc. may be used to endorse or promote
 *   products derived from this software without explicit prior
 *   written permission.
 *
 * You are under no obligation whatsoever to provide any enhancements
 * to the University of Chicago, its contributors, or the University
 * Corporation for Advanced Internet Development, Inc.  If you choose
 * to provide your enhancements, or if you choose to otherwise publish
 * or distribute your enhancements, in source code form without
 * contemporaneously requiring end users to enter into a separate
 * written license agreement for such enhancements, then you thereby
 * grant the University of Chicago, its contributors, and the University
 * Corporation for Advanced Internet Development, Inc. a non-exclusive,
 * royalty-free, perpetual license to install, use, modify, prepare
 * derivative works, incorporate into the software or other computer
 * software, distribute, and sublicense your enhancements or derivative
 * works thereof, in binary and source code form.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND WITH ALL FAULTS.  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
 * BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE, AND NON-INFRINGEMENT ARE DISCLAIMED AND the
 * entire risk of satisfactory quality, performance, accuracy, and effort
 * is with LICENSEE. IN NO EVENT SHALL THE COPYRIGHT OWNER, CONTRIBUTORS,
 * OR THE UNIVERSITY CORPORATION FOR ADVANCED INTERNET DEVELOPMENT, INC.
 * BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR
 * BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OR DISTRIBUTION OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package test.edu.internet2.middleware.grouper;

import  edu.internet2.middleware.grouper.*;
import  edu.internet2.middleware.subject.*;
import  junit.framework.*;

public class TestAccessGrantMoF2 extends TestCase {

  public TestAccessGrantMoF2(String name) {
    super(name);
  }

  protected void setUp () {
    DB db = new DB();
    db.emptyTables();
    db.stop();
  }

  protected void tearDown () {
    // Nothing -- Yet
  }


  /*
   * TESTS
   */
  

  //
  // Add m0 to g0
  // Add g0 to g1
  //
  // m0 -> g0 -> g1
  //
  public void testMoF() {
    Subject subj = SubjectFactory.load(Constants.rootI, Constants.rootT);
    GrouperSession s = GrouperSession.start(subj);

    // Create ns0
    GrouperStem ns0 = GrouperStem.create(
                         s, Constants.ns0s, Constants.ns0e
                       );
    // Create ns1
    GrouperStem ns1 = GrouperStem.create(
                         s, Constants.ns1s, Constants.ns1e
                       );
    // Create g0
    GrouperGroup g0  = GrouperGroup.create(
                         s, Constants.g0s, Constants.g0e
                       );
    // Create g1
    GrouperGroup g1  = GrouperGroup.create(
                         s, Constants.g1s, Constants.g1e
                       );
    // Load m0
    GrouperMember m0 = GrouperMember.load(
                         s, Constants.mem0I, Constants.mem0T
                       );
    // Load m1
    GrouperMember m1 = GrouperMember.load(
                         s, Constants.mem1I, Constants.mem1T
                       );

    // Grant m0 ADMIN on g0
    Assert.assertTrue(
      "grant m0 ADMIN on g0", 
      s.access().grant(s, g0, m0, Grouper.PRIV_ADMIN)
    );
    // Grant g0 ADMIN on g1
    Assert.assertTrue(
      "grant g0 ADMIN on g1", 
      s.access().grant(s, g1, g0.toMember(), Grouper.PRIV_ADMIN)
    );


    // Assert privileges
    Assert.assertTrue(
      "g0 has == 0 privs on g0", 
      s.access().has(s, g0, g0.toMember()).size() == 0
    );
    Assert.assertFalse( 
      "g0 !ADMIN on g0",
      s.access().has(s, g0, g0.toMember(), Grouper.PRIV_ADMIN)
    );
    Assert.assertFalse( 
      "g0 !UPDATE on g0",
      s.access().has(s, g0, g0.toMember(), Grouper.PRIV_UPDATE)
    );

    Assert.assertTrue(
      "g1 has == 0 privs on g0", 
      s.access().has(s, g0, g1.toMember()).size() == 0
    );
    Assert.assertFalse( 
      "g1 !ADMIN on g0",
      s.access().has(s, g0, g1.toMember(), Grouper.PRIV_ADMIN)
    );
    Assert.assertFalse( 
      "g1 !UPDATE on g0",
      s.access().has(s, g0, g1.toMember(), Grouper.PRIV_UPDATE)
    );

    Assert.assertTrue(
      "root has == 6 privs on g0", 
      s.access().has(s, g0).size() == 6
    );
    Assert.assertTrue(
      "root ADMIN on g0",
      s.access().has(s, g0, Grouper.PRIV_ADMIN)
    );
    Assert.assertTrue(
      "root UPDATE on g0",
      s.access().has(s, g0, Grouper.PRIV_UPDATE)
    );

    Assert.assertTrue(
      "m0 has == 1 privs on g0", 
      s.access().has(s, g0, m0).size() == 1
    );
    Assert.assertTrue(
      "m0 ADMIN on g0", 
      s.access().has(s, g0, m0, Grouper.PRIV_ADMIN)
    );
    Assert.assertFalse(
      "m0 !UPDATE on g0", 
      s.access().has(s, g0, m0, Grouper.PRIV_UPDATE)
    );

    Assert.assertTrue(
      "m1 has == 0 privs on g0", 
      s.access().has(s, g0, m1).size() == 0
    );
    Assert.assertFalse(
      "m1 !ADMIN on g0", 
      s.access().has(s, g0, m1, Grouper.PRIV_ADMIN)
    );
    Assert.assertFalse( 
      "m1 !UPDATE on g0",
      s.access().has(s, g0, m1, Grouper.PRIV_UPDATE)
    );

    Assert.assertTrue(
      "g0 has == 1 privs on g1", 
      s.access().has(s, g1, g0.toMember()).size() == 1
    );
    Assert.assertTrue( 
      "g0 ADMIN on g1",
      s.access().has(s, g1, g0.toMember(), Grouper.PRIV_ADMIN)
    );
    Assert.assertFalse( 
      "g0 !UPDATE on g1",
      s.access().has(s, g1, g0.toMember(), Grouper.PRIV_UPDATE)
    );

    Assert.assertTrue(
      "g1 has == 0 privs on g1", 
      s.access().has(s, g1, g1.toMember()).size() == 0
    );
    Assert.assertFalse( 
      "g1 !ADMIN on g1",
      s.access().has(s, g1, g1.toMember(), Grouper.PRIV_ADMIN)
    );
    Assert.assertFalse( 
      "g1 !UPDATE on g1",
      s.access().has(s, g1, g1.toMember(), Grouper.PRIV_UPDATE)
    );

    Assert.assertTrue(
      "root has == 6 privs on g1", 
      s.access().has(s, g1).size() == 6
    );
    Assert.assertTrue(
      "root ADMIN on g1",
      s.access().has(s, g1, Grouper.PRIV_ADMIN)
    );
    Assert.assertTrue(
      "root UPDATE on g1",
      s.access().has(s, g1, Grouper.PRIV_UPDATE)
    );

    Assert.assertTrue(
      "m0 has == 0 privs on g1", 
      s.access().has(s, g1, m0).size() == 0
    );
    Assert.assertFalse(
      "m0 !ADMIN on g1", 
      s.access().has(s, g1, m0, Grouper.PRIV_ADMIN)
    );
    Assert.assertFalse(
      "m0 !UPDATE on g1", 
      s.access().has(s, g1, m0, Grouper.PRIV_UPDATE)
    );

    Assert.assertTrue(
      "m1 has == 0 privs on g1", 
      s.access().has(s, g1, m1).size() == 0
    );
    Assert.assertFalse(
      "m1 !ADMIN on g1", 
      s.access().has(s, g1, m1, Grouper.PRIV_ADMIN)
    );
    Assert.assertFalse( 
      "m1 !UPDATE on g1",
      s.access().has(s, g1, m1, Grouper.PRIV_UPDATE)
    );

    s.stop();
  }

}


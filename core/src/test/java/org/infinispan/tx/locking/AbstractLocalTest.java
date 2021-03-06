package org.infinispan.tx.locking;

import org.infinispan.test.SingleCacheManagerTest;
import org.infinispan.transaction.tm.DummyTransaction;
import org.infinispan.transaction.tm.DummyTransactionManager;
import org.testng.annotations.Test;

import javax.transaction.SystemException;
import javax.transaction.xa.Xid;
import java.util.Collections;
import java.util.Map;

import static org.testng.Assert.assertNull;

/**
 * @author Mircea Markus
 * @since 5.1
 */
@Test(groups = "functional")
public abstract class AbstractLocalTest extends SingleCacheManagerTest {

   public void testPut() throws Exception {
      tm().begin();
      cache.put("k", "v");
      assertLocking();
   }

   public void testRemove() throws Exception {
      tm().begin();
      cache.remove("k");
      assertLocking();
   }

   public void testReplace() throws Exception {
      cache.put("k", "initial");
      tm().begin();
      cache.replace("k", "v");
      assertLocking();
   }

   public void testClear() throws Exception {
      cache.put("k", "v");
      tm().begin();
      cache.clear();
      assertLocking();
   }

   public void testPutAll() throws Exception {
      Map m = Collections.singletonMap("k", "v");
      tm().begin();
      cache.putAll(m);
      assertLocking();
   }

   public void testRollback() throws Exception {
      tm().begin();
      cache().put("k", "v");
      assertLockingOnRollback();
      assertNull(cache().get("k"));
   }

   protected abstract void assertLockingOnRollback();

   protected abstract void assertLocking();

   protected void commit() {
      DummyTransactionManager dtm = (DummyTransactionManager) tm();
      try {
         dtm.firstEnlistedResource().commit(getXid(), true);
      } catch (Throwable e) {
         throw new RuntimeException(e);
      }
   }

   protected void prepare() {
      DummyTransactionManager dtm = (DummyTransactionManager) tm();
      try {
         dtm.firstEnlistedResource().prepare(getXid());
      } catch (Throwable e) {
         throw new RuntimeException(e);
      }
   }

   protected void rollback() {
      DummyTransactionManager dtm = (DummyTransactionManager) tm();
      try {
         dtm.getTransaction().rollback();
      } catch (SystemException e) {
         throw new RuntimeException(e);
      }
   }

   private Xid getXid() throws SystemException {
      Xid xid;DummyTransaction dummyTransaction = (DummyTransaction) tm().getTransaction();
      xid = dummyTransaction.getXid();
      return xid;
   }
}

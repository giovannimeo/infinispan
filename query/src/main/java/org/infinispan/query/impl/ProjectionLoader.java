/*
 * JBoss, Home of Professional Open Source
 * Copyright 2012 Red Hat Inc. and/or its affiliates and other
 * contributors as indicated by the @author tags. All rights reserved.
 * See the copyright.txt in the distribution for a full listing of
 * individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.infinispan.query.impl;

import org.hibernate.search.query.engine.spi.EntityInfo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author <a href="mailto:mluksa@redhat.com">Marko Luksa</a>
 */
public class ProjectionLoader implements QueryResultLoader {

   private final ProjectionConverter projectionConverter;
   private final EntityLoader entityLoader;

   public ProjectionLoader(ProjectionConverter projectionConverter, EntityLoader entityLoader) {
      this.projectionConverter = projectionConverter;
      this.entityLoader = entityLoader;
   }

   @Override
   public List<Object> load(Collection<EntityInfo> entityInfos) {
      List<Object> list = new ArrayList<Object>(entityInfos.size());
      for (EntityInfo entityInfo : entityInfos) {
         list.add( load( entityInfo ) );
      }
      return list;
   }

   public Object[] load(EntityInfo entityInfo) {
      Object[] projection = entityInfo.getProjection();
      if (entityInfo.isProjectThis()) {
         entityInfo.populateWithEntityInstance(entityLoader.load(entityInfo));
      }
      return projectionConverter.convert(projection);
   }
}

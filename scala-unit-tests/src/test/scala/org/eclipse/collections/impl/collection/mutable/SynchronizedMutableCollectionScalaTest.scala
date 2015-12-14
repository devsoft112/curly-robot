/*
 * Copyright (c) 2015 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.collection.mutable

import org.eclipse.collections.api.collection.MutableCollection
import org.eclipse.collections.impl.list.mutable.FastList

class SynchronizedMutableCollectionScalaTest extends SynchronizedMutableCollectionTestTrait
{
    val collection: MutableCollection[String] = FastList.newListWith[String]("1", "2", "3")
    val classUnderTest: MutableCollection[String] = SynchronizedMutableCollection.of[String, MutableCollection[String]](this.collection)
}

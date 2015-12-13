/*
 * Copyright (c) 2015 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.lazy.parallel;

import org.eclipse.collections.api.annotation.Beta;
import org.eclipse.collections.api.block.predicate.Predicate;

/**
 * A {@link Batch} that must be at the root of the chain, not wrapped in other Batches.
 */
@Beta
public interface RootBatch<T> extends Batch<T>
{
    boolean anySatisfy(Predicate<? super T> predicate);

    boolean allSatisfy(Predicate<? super T> predicate);

    T detect(Predicate<? super T> predicate);
}

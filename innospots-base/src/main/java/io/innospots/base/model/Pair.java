/*
 *  Copyright © 2021-2023 Innospots (http://www.innospots.com)
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License. You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package io.innospots.base.model;

import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * @author Smars
 * @date 2021/9/19
 */
public class Pair<S, T> {
    private S left;
    private T right;

    private Pair(S left, T right) {
        Assert.notNull(left, "Left must not be null!");
        Assert.notNull(right, "Right must not be null!");
        this.left = left;
        this.right = right;
    }

    public Pair() {
    }

    public static <S, T> Pair<S, T> of(S left, T right) {
        return new Pair(left, right);
    }

    public S getLeft() {
        return this.left;
    }

    public T getRight() {
        return this.right;
    }

    public void setLeft(S left) {
        this.left = left;
    }

    public void setRight(T right) {
        this.right = right;
    }

    public static <S, T> Collector<Pair<S, T>, ?, Map<S, T>> toMap() {
        return Collectors.toMap(Pair::getLeft, Pair::getRight);
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) {
            return true;
        } else if (!(o instanceof Pair)) {
            return false;
        } else {
            Pair<?, ?> pair = (Pair) o;
            return !ObjectUtils.nullSafeEquals(this.left, pair.left) ? false : ObjectUtils.nullSafeEquals(this.right, pair.right);
        }
    }

    @Override
    public int hashCode() {
        int result = ObjectUtils.nullSafeHashCode(this.left);
        result = 31 * result + ObjectUtils.nullSafeHashCode(this.right);
        return result;
    }

    @Override
    public String toString() {
        return String.format("%s->%s", this.left, this.right);
    }
}

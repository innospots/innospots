/*
 * Datart
 * <p>
 * Copyright 2021
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package datart.data.provider.jdbc;

import datart.data.provider.script.ReplacementPair;
import datart.data.provider.script.VariablePlaceholder;
import datart.provider.ScriptVariable;
import org.apache.calcite.sql.SqlDialect;
import org.apache.calcite.sql.SqlIdentifier;

public class SimpleVariablePlaceholder extends VariablePlaceholder {

    private ScriptVariable variable;

    private SqlIdentifier identifier;

    public SimpleVariablePlaceholder(ScriptVariable variable, SqlDialect sqlDialect, String originalSqlFragment) {
        super(null, sqlDialect, null, originalSqlFragment);
        this.variable = variable;
    }

    @Override
    public ReplacementPair replacementPair() {
        if (variable == null) {
            return new ReplacementPair(originalSqlFragment, originalSqlFragment);
        }
        return new ReplacementPair(originalSqlFragment, formatValue(variable));
    }

    @Override
    public int getStartPos() {
        return Integer.MAX_VALUE;
    }
}

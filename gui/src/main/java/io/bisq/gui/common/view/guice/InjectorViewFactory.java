/*
 * This file is part of bisq.
 *
 * bisq is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at
 * your option) any later version.
 *
 * bisq is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with bisq. If not, see <http://www.gnu.org/licenses/>.
 */

package io.bisq.gui.common.view.guice;

import com.google.common.base.Preconditions;
import com.google.inject.Injector;
import io.bisq.gui.common.view.ViewFactory;

public class InjectorViewFactory implements ViewFactory {

    private Injector injector;

    public void setInjector(Injector injector) {
        this.injector = injector;
    }

    @Override
    public Object call(Class<?> aClass) {
        Preconditions.checkNotNull(injector, "Injector has not yet been provided");
        return injector.getInstance(aClass);
    }
}

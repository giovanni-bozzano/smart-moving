// ==================================================================
// This file is part of Smart Moving.
//
// Smart Moving is free software: you can redistribute it and/or
// modify it under the terms of the GNU General Public License as
// published by the Free Software Foundation, either version 3 of the
// License, or (at your option) any later version.
//
// Smart Moving is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with Smart Moving. If not, see <http://www.gnu.org/licenses/>.
// ==================================================================
package net.smart.moving.render;

public interface IModelPlayer
{
    CustomModel getMovingModel();

    void superAnimateHeadRotation(float totalHorizontalDistance, float currentHorizontalSpeed, float totalTime, float viewHorizontalAngelOffset, float viewVerticalAngelOffset, float factor);

    void superAnimateSleeping(float totalHorizontalDistance, float currentHorizontalSpeed, float totalTime, float viewHorizontalAngelOffset, float viewVerticalAngelOffset, float factor);

    void superAnimateArmSwinging(float totalHorizontalDistance, float currentHorizontalSpeed, float totalTime, float viewHorizontalAngelOffset, float viewVerticalAngelOffset, float factor);

    void superAnimateRiding(float totalHorizontalDistance, float currentHorizontalSpeed, float totalTime, float viewHorizontalAngelOffset, float viewVerticalAngelOffset, float factor);

    void superAnimateLeftArmItemHolding(float totalHorizontalDistance, float currentHorizontalSpeed, float totalTime, float viewHorizontalAngelOffset, float viewVerticalAngelOffset, float factor);

    void superAnimateRightArmItemHolding(float totalHorizontalDistance, float currentHorizontalSpeed, float totalTime, float viewHorizontalAngelOffset, float viewVerticalAngelOffset, float factor);

    void superAnimateWorkingBody(float totalHorizontalDistance, float currentHorizontalSpeed, float totalTime, float viewHorizontalAngelOffset, float viewVerticalAngelOffset, float factor);

    void superAnimateWorkingArms(float totalHorizontalDistance, float currentHorizontalSpeed, float totalTime, float viewHorizontalAngelOffset, float viewVerticalAngelOffset, float factor);

    void superAnimateSneaking(float totalHorizontalDistance, float currentHorizontalSpeed, float totalTime, float viewHorizontalAngelOffset, float viewVerticalAngelOffset, float factor);

    void superApplyAnimationOffsets(float totalHorizontalDistance, float currentHorizontalSpeed, float totalTime, float viewHorizontalAngelOffset, float viewVerticalAngelOffset, float factor);

    void superAnimateBowAiming(float totalHorizontalDistance, float currentHorizontalSpeed, float totalTime, float viewHorizontalAngelOffset, float viewVerticalAngelOffset, float factor);
}
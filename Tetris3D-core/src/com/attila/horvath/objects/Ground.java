package com.attila.horvath.objects;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.RenderableProvider;
import com.badlogic.gdx.utils.Disposable;

public class Ground extends ModelInstance implements RenderableProvider,
		Disposable {
	
	public Ground(Model model) {
		super(model);

		setBasics();
	}

	private void setBasics() {
		this.transform.trn(0, -80, 0);
		this.transform.rotate(0, 1, 0, 45);
	}
	
	@Override
	public void dispose() {
		this.dispose();
	}
}

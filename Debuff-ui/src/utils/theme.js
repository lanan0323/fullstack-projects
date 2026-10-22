// 处理主题样式，设置 CSS 变量实现全局换肤
export function handleThemeStyle(theme) {
	// 设置主色调 CSS 变量
	document.documentElement.style.setProperty('--el-color-primary', theme)
	// 生成 9 级渐浅色变量
	for (let i = 1; i <= 9; i++) {
		document.documentElement.style.setProperty(`--el-color-primary-light-${i}`, `${getLightColor(theme, i / 10)}`)
	}
	// 生成 9 级渐深色变量
	for (let i = 1; i <= 9; i++) {
		document.documentElement.style.setProperty(`--el-color-primary-dark-${i}`, `${getDarkColor(theme, i / 10)}`)
	}
}

// HEX 颜色值转 RGB 数组
export function hexToRgb(str) {
	// 去掉 # 号
	str = str.replace('#', '')
	// 每两位切分为一组
	let hexs = str.match(/../g)
	// 将十六进制转为十进制
	for (let i = 0; i < 3; i++) {
		hexs[i] = parseInt(hexs[i], 16)
	}
	return hexs
}

// RGB 颜色值转 HEX 字符串
export function rgbToHex(r, g, b) {
	// 将每个通道转为十六进制
	let hexs = [r.toString(16), g.toString(16), b.toString(16)]
	// 不足两位补零
	for (let i = 0; i < 3; i++) {
		if (hexs[i].length == 1) {
			hexs[i] = `0${hexs[i]}`
		}
	}
	return `#${hexs.join('')}`
}

// 计算变浅颜色（向白色靠近）
export function getLightColor(color, level) {
	// 转为 RGB
	let rgb = hexToRgb(color)
	// 每个通道向 255 靠近 level 比例
	for (let i = 0; i < 3; i++) {
		rgb[i] = Math.floor((255 - rgb[i]) * level + rgb[i])
	}
	return rgbToHex(rgb[0], rgb[1], rgb[2])
}

// 计算变深颜色（向黑色靠近）
export function getDarkColor(color, level) {
	// 转为 RGB
	let rgb = hexToRgb(color)
	// 每个通道向 0 靠近 level 比例
	for (let i = 0; i < 3; i++) {
		rgb[i] = Math.floor(rgb[i] * (1 - level))
	}
	return rgbToHex(rgb[0], rgb[1], rgb[2])
}

	/**
	 * Generate avatar color based on username
	 */
	private static String generateAvatarColor(String username) {
		def colors = ['#FF6B6B', '#4ECDC4', '#45B7D1', '#96CEB4', '#FFEEAD', 
		              '#D4A5A5', '#9B59B6', '#3498DB', '#E67E22', '#2ECC71']
		int hash = username.hashCode()
		return colors[Math.abs(hash) % colors.size()]
	}
	
	/**
	 * Generate avatar URL using DiceBear API
	 */
	private static String generateAvatarUrl(String username, String color) {
		String initials = extractInitials(username)
		return "https://api.dicebear.com/9.x/initials/svg?seed=" + username + "&name=" + initials + "&backgroundColor=" + color.replace('#', '')
	}
	
	/**
	 * Extract initials from username
	 */
	private static String extractInitials(String username) {
		def parts = username.split(/[\.\\-_]/)
		if (parts.size() >= 2) {
			return "${parts[0][0]}${parts[-1][0]}".toUpperCase()
		}
		return username.substring(0, Math.min(2, username.length())).toUpperCase()
	}

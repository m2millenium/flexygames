package flexygames


import grails.converters.JSON
import grails.gorm.transactions.Transactional

import java.text.SimpleDateFormat
import java.util.regex.Matcher

import org.apache.shiro.crypto.hash.Sha512Hash
import org.springframework.web.multipart.MultipartHttpServletRequest

class PlayerController {

	def displayService

	def mailerService

	def userStatsService

	def calendarService

	def databaseSeedingService
	
	def index() { redirect(action:"list") }

	def home() { redirect(action:"list") }

	def list() {
		if (!params.max || params.max == 'null') params.max = 20
		params.max = Math.min(params.max ? params.int('max') : 20, 100)
		if(!params.sort) params.sort = "username"
		if(!params.order) params.order = "asc"
		def users = User.list(params)
		def total = User.count()
		if (params.sort == "username") {
			if (!params.first) params.first = ''
			users = User.findAllByUsernameIlike(params.first + "%", params)
			total = User.countByUsernameIlike(params.first + "%")
		}
		[playerInstanceList: users, playerInstanceTotal: total]
	}

	def jsonList() {
		def users = User.list(sort: 'username', order: 'asc')
		def json = []
		users.each { u ->
			json << [id: u.id, username: u.username]
		}
		render  json as JSON
	}

	def show() {
		def playerInstance = User.get(params.id)
		if (!playerInstance) {
			flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'player.label', default: 'Player'), params.id])}"
			redirect(action: "list")
		}
		else {
			render(view: (displayService.isMobileDevice(request) ? 'mobileShow' : 'show'), model: [playerInstance: playerInstance])
		}
	}

	def stats() {
		def player = User.get(params.id)
		if (!player) {
			flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'player.label', default: 'Player'), params.id])}"
			redirect(action: "list")
		}
		else {
			if (params.groupId) {
				def SessionGroup group = SessionGroup.get(params.groupId)
				if (!group) {
					flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'player.label', default: 'Player'), params.id])}"
				}
				return render(view: 'statsForGroup', model: [playerInstance: player, group: group])
			} else {
				def userStats = userStatsService.getUserStats(player)
				return [userStats:userStats]
			}
		}
	}

	def register() {}

	@Transactional
	def save() {
		User user = new User(params)
		User similarUser = User.findByUsernameIlike(params.username.toLowerCase())
		if (similarUser) {
			flash.message = "${message(code:'register.similarUserAlreadyExists', args: [similarUser.username])}"
			return render(view: "register", model: [user: similarUser])
		}
		Matcher m = User.USERNAME_VALID_CHARS.matcher(params.username)
		if (!m.matches()) {
			flash.message = "${message(code: 'register.invalidUsername')}"
			return render(view: "register", model: [user: user])
		}
		if (!params.password || params.password.length() < 4) {
			flash.message = "${message(code: 'register.invalidPassword')}"
			return render(view: "register", model: [user: user])
		}
		Team team = null
		if (params.teamId) {
			team = Team.get(params.teamId)
			if (!team) {
				flash.message = "Team with id [$params.teamId] doesn't exist !"
				return render(view: "register", model: [user: user])
			}
		}
		user.passwordHash = new Sha512Hash(params.password).toHex()
		user.firstName = params.firstName
		user.email = params.email.toLowerCase()
		user.registrationDate = new Date()
		user.avatarName = 'no-avatar.jpg'
		user.avatarColor = generateAvatarColor(params.username)
		user.avatarUrl = generateAvatarUrl(params.username, user.avatarColor)
		user.addToRoles(Role.findByName("Player"))
		if (team) {
			Membership ms = new Membership(user: user, team: team, manager: false,
					regularForTraining: true,
					regularForCompetition: false)
			user.addToMemberships(ms)
		}
		user.commentCounter = 0
		user.participationCounter = 0
		user.voteCounter = 0
		user.actionCounter = 0
		user.absenceCounter = 0
		user.delayCounter = 0
		user.gateCrashCounter = 0
		user.waitingListCounter = 0
		if (!user.save()) {
			flash.message = "${message(code: 'register.failed')}"
			return render(view: "register", model: [user: user])
		}
		flash.message = "${message(code: 'register.success')}"
		redirect(controller:"site", action: "home")
	}

	def remindPassword() {}

	@Transactional
	def sendPasswordReset() {
		User user = User.findByUsernameOrEmail(params.username, params.email.toLowerCase())
		if (!user) {
			flash.message = "${message(code: 'remindPassword.userNotFound')}"
			return redirect(action: "remindPassword")
		}
		user.passwordResetToken = UUID.randomUUID().toString()
		user.passwordResetExpiration = new Date() + 1
		if (!user.save()) {
			flash.message = "Sorry, unable to save password token ! <br><br>$user.errors"
			return redirect(action: "remindPassword")
		}
		def body = 'Hi ' + user + ', for reset your password please follow that <a href="' +
					grailsApplication.config.grails.serverURL + '/player/resetPassword?id=' + user.id +
					'&token=' + user.passwordResetToken + '">link</a>.'
		mailerService.mail(user.email, "Scatterbrain !!", body)
		flash.message = "${message(code: 'remindPassword.success')}"
		redirect(controller:"site", action: "home")
	}

	def resetPassword() {}

	@Transactional
	def refreshPassword() {
		User user = User.findByIdAndPasswordResetToken(params.id, params.token)
		if (!user) {
			flash.message = "${message(code: 'resetPassword.userNotFound')}"
			return redirect(action: "resetPassword")
		}
		if (user.passwordResetExpiration.before(new Date())) {
			flash.message = "${message(code: 'resetPassword.expiredToken')}"
			return redirect(action: "resetPassword")
		}
		user.passwordHash = new Sha512Hash(params.newPassword).toHex()
		user.passwordResetToken = "Reset already done"
		user.passwordResetExpiration = new Date()
		if (!user.save()) {
			flash.message = "Sorry, unable to save your new password ! <br><br>$user.errors"
			return redirect(action: "resetPassword")
		}
		flash.message = "${message(code: 'resetPassword.success')}"
		redirect(controller:"site", action: "home")
	}
	
	def cal() {
			def player = User.get(params.id)
			if (!player) {
				flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'player.label', default: 'Player'), params.id])}"
				redirect(action: "list")
			}
			if (player.calendarToken != params.token) {
				flash.message = "Sorry, your calendar link is invalid.. are you trying to spy $player ? :)"
				redirect(action: "show", id: player.id)
			}
			def calendar = calendarService.getPlayerCalendar(player)
			response.addHeader("content-disposition", String.format("attachment;filename=%s", "FlexyCalendarFor"+player.username + ".ics"))
			println new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + " FlexyGames : Calendar generated for $player"
			return calendar
		}

	def seedDatabase() {
		DeveloperDataSeeder.seed()
		render "Developer data seeded successfully!"
	}

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
}
